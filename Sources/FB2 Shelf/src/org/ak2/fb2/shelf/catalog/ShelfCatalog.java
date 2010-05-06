package org.ak2.fb2.shelf.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.collections.SafeSortedMap;
import org.ak2.utils.collections.factories.IMapValueFactory;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class ShelfCatalog implements Iterable<BookInfo> {

    private static final JLogMessage MSG_LOAD_START = new JLogMessage(JLogLevel.DEBUG, "Loading catalog from {0} started...");

    private static final JLogMessage MSG_LOAD_ERROR = new JLogMessage(JLogLevel.ERROR, "Loading catalog from {0} failed: ");

    private static final JLogMessage MSG_LOAD_FINISH = new JLogMessage(JLogLevel.DEBUG, "Loading catalog from {0} finished.");

    private final File m_original;

    private final List<BookInfo> m_books = new LinkedList<BookInfo>();

    private final Map<BookAuthor, List<BookInfo>> m_authors = new SafeSortedMap<BookAuthor, List<BookInfo>>(new IMapValueFactory<BookAuthor, List<BookInfo>>() {
        @Override
        public List<BookInfo> create(final BookAuthor key) {
            return new LinkedList<BookInfo>();
        }
    });

    private final Map<BookAuthor, Set<String>> m_sequences = new SafeSortedMap<BookAuthor, Set<String>>(new IMapValueFactory<BookAuthor, Set<String>>() {
        @Override
        public Set<String> create(final BookAuthor key) {
            return new TreeSet<String>();
        }
    });

    public ShelfCatalog(final File xmlCatalog) {
        m_original = xmlCatalog;
        if (MSG_LOAD_START.isEnabled()) {
            MSG_LOAD_START.log(m_original.getName());
        }
        try {
            final InputStreamReader r = new InputStreamReader(new FileInputStream(m_original), "UTF-8");
            final JSONObject jsonObject = XML.toJSONObject(r);

            final JSONObject root = jsonObject.getJSONObject("books");
            final JSONObject location = root.getJSONObject("location");

            String locationBase = location.getString("base");
            locationBase = ShelfCatalogProvider.getLocationMapping(locationBase);

            final JSONArray books = location.getJSONArray("book");

            for (int i = 0; i < books.length(); i++) {
                final JSONObject book = books.getJSONObject(i);
                addBook(new BookInfo(locationBase, book));
            }
        } catch (final Exception ex) {
            MSG_LOAD_ERROR.log(ex, m_original.getName());
        } finally {

            Collections.sort(m_books);

            for (List<BookInfo> books : m_authors.values()) {
                Collections.sort(books);
            }

            if (MSG_LOAD_FINISH.isEnabled()) {
                MSG_LOAD_FINISH.log(m_original.getName());
            }
        }
    }

    public void addBook(final BookInfo book) {
        final BookAuthor author = book.getAuthor();
        final String sequence = book.getSequence();

        m_authors.get(author).add(book);

        if (LengthUtils.isNotEmpty(sequence)) {
            m_sequences.get(author).add(sequence);
        }

        m_books.add(book);
    }

    public Collection<BookAuthor> getAuthors() {
        return m_authors.keySet();
    }

    public List<BookInfo> getBooks() {
        return m_books;
    }

    public List<BookInfo> getBooks(final BookAuthor author) {
        return m_authors.get(author);
    }

    public Set<String> getSequences(final BookAuthor author) {
        return m_sequences.get(author);
    }

    @Override
    public Iterator<BookInfo> iterator() {
        return m_books.iterator();
    }
}
