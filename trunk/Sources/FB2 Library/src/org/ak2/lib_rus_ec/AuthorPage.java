package org.ak2.lib_rus_ec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.utils.LengthUtils;

public class AuthorPage {

    private static final String PATTERN = "<h1 class=\"title\">([^<]+)</h1>|<a name=(\\w+)><a class=genre href=\\/g\\/\\w+><h9>([^<]+)<\\/h9>|<a href=\\/s\\/\\d+><h8>([^<]+)</h8>|<input type=checkbox  id='[\\w-]+' name=\\d+>\\s+-\\s+((\\d+)\\.\\s*)?<a href=(\\/b\\/\\d+)>([^<]+)<\\/a>";

    private final URL m_authorUrl;

    private BookAuthor m_author;

    private final String m_id;

    public AuthorPage(final BookAuthor author, final String id) throws MalformedURLException {
        m_author = author;
        m_id = id;
        m_authorUrl = new URL("http://" + LibRusEc.SITE + LibRusEc.AUTHOR_PATH + id);
    }

    public AuthorPage(final URL authorUrl) {
        m_authorUrl = authorUrl;
        m_id = LibRusEc.getId(m_authorUrl);
    }

    public AuthorPage(String name, URL authorUrl) {
        super();
        m_author = new BookAuthor(name);
        m_authorUrl = authorUrl;
        m_id = LibRusEc.getId(m_authorUrl);
    }

    /**
     * @return the id
     */
    public final String getId() {
        return m_id;
    }

    public List<BookPage> getBooks() throws IOException {
        final List<BookPage> list = new LinkedList<BookPage>();

        final URLConnection conn = m_authorUrl.openConnection();
        final StringBuilder buf = new StringBuilder();
        final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
        for (String s = in.readLine(); s != null; s = in.readLine()) {
            buf.append(s).append('\n');
        }

        String currentGenre = null;
        String currentSequence = null;

        final Pattern p = Pattern.compile(PATTERN, Pattern.DOTALL);
        final Matcher m = p.matcher(buf);
        for (int start = 0; m.find(start); start = m.end()) {
            if (LengthUtils.isNotEmpty(m.group(1))) {
                m_author = new BookAuthor(m.group(1), false);
            } else if (LengthUtils.isNotEmpty(m.group(2))) {
                currentGenre = m.group(2);
            } else if (LengthUtils.isNotEmpty(m.group(4))) {
                currentSequence = m.group(4);
            } else {
                final String seqNo = m.group(6);
                final String link = m.group(7);
                final String name = m.group(8);
                final BookPage book = new BookPage(this, name, currentGenre, currentSequence, seqNo, link);
                list.add(book);
            }

        }
        return list;
    }

    public URL getAuthorUrl() {
        return m_authorUrl;
    }

    public BookAuthor getAuthor() {
        return m_author;
    }

    @Override
    public String toString() {
        return "AuthorPage [m_author=" + m_author + ", m_authorUrl=" + m_authorUrl + "]";
    }

}