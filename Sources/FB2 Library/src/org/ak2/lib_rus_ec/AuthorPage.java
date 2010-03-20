package org.ak2.lib_rus_ec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.Web;

public class AuthorPage {

    private static final String PATTERN = "<h1 class=\\\"title\\\">([^<]+)</h1>|<a name=(\\w+)><a class=genre href=\\/g\\/\\w+><h9>([^<]+)<\\/h9>|<a class=sequence href=\\/s\\/\\d+><h8>([^<]+)</h8>|<img src=/img/znak.gif border=0>\\s+-\\s+((\\d+)\\.\\s*)?<a href=(\\/b\\/\\d+)>([^<]+)<\\/a>";

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

        final IWebContent content = Web.get(m_authorUrl, m_author.getName());
        final String text = StreamUtils.getText(content.getReader());

        String currentGenre = null;
        String currentSequence = null;

        final Pattern p = Pattern.compile(PATTERN, Pattern.DOTALL);
        final Matcher m = p.matcher(text);
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
