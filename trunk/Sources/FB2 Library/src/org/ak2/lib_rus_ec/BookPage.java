package org.ak2.lib_rus_ec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.ak2.fb2.library.book.XmlContent;
import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.Web;

public class BookPage {

    private final AuthorPage m_authorPage;

    private final String m_genre;

    private final String m_sequence;

    private final String m_seqNo;

    private final String m_name;

    private final String m_link;

    private final String m_id;

    private Set<BookImage> m_images = Collections.emptySet();

    public BookPage(final AuthorPage authorPage, final String name, final String genre, final String sequence, final String seqNo, final String link) {
        super();
        m_authorPage = authorPage;
        m_name = name;
        m_genre = genre;
        m_sequence = sequence;
        m_seqNo = seqNo;
        m_link = link;
        m_id = LibRusEc.getId(link);
    }

    public AuthorPage getAuthorPage() {
        return m_authorPage;
    }

    public String getLink() {
        return m_link;
    }

    public String getName() {
        return m_name;
    }

    public final String getId() {
        return m_id;
    }

    public String getGenre() {
        return m_genre;
    }

    public String getSequence() {
        return m_sequence;
    }

    public String getSeqNo() {
        return m_seqNo;
    }

    public Set<BookImage> getImages() {
        return m_images;
    }

    void setImages(Set<BookImage> images) {
        m_images = images;
    }

    public XmlContent getContent() throws IOException {
        return getContent(new BookPageTransformer());
    }

    public XmlContent getContent(BookPageTransformer t) throws IOException {
        final URL authorUrl = m_authorPage != null ? m_authorPage.getAuthorUrl() : null;
        if (authorUrl == null) {
            throw new IllegalArgumentException("Book author URL unknown");
        }
        final URL url = new URL(authorUrl.getProtocol(), authorUrl.getHost(), m_link + "/read");
        final IWebContent conn = Web.get(url);
        return getContent(conn.getReader(), t);
    }

    XmlContent getContent(final InputStream input, final String encoding) throws UnsupportedEncodingException, IOException {
        return getContent(new InputStreamReader(input, encoding), new BookPageTransformer());
    }

    public XmlContent getContent(final Reader reader, BookPageTransformer t) throws UnsupportedEncodingException, IOException {
        final StringBuilder buf = StreamUtils.loadText(reader, new Loader());
        return t.transform(this, buf);
    }

    @Override
    public String toString() {
        return "BookPage [m_authorPage=" + m_authorPage + ", m_name=" + m_name + ", m_genre=" + m_genre + ", m_sequence=" + m_sequence + ", m_seqNo=" + m_seqNo
                + ", m_link=" + m_link + "]";
    }

    static final class Loader extends StreamUtils.TextLoader {
        private boolean reachBook = false;
        private boolean reachEnd = false;

        @Override
        public void reset() {
            super.reset();
            reachBook = false;
            reachEnd = false;
        }

        @Override
        public boolean onLine(final String line) {
            String s = line;
            if (!reachBook) {
                final int ind = s.indexOf("<h3 class=book>");
                if (ind >= 0) {
                    s = s.substring(ind);
                    reachBook = true;
                }
            }
            if (reachBook) {
                final int ind1 = s.indexOf("<!-- topadvert.ru -->");
                final int ind2 = s.indexOf("<h3>");
                if (ind1 >= 0) {
                    s = s.substring(0, ind1);
                    reachEnd = true;
                } else if (ind2 >= 0) {
                    s = s.substring(0, ind2);
                    reachEnd = true;
                }
                super.onLine(s);
            }
            return !reachEnd;
        }
    }

}
