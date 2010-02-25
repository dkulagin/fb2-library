package org.ak2.fb2.importt.lib_rus_ec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.ma.Author;
import org.ak2.utils.LengthUtils;

public class BookPage {

    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<String, String>();

    static {
        REPLACEMENTS.put("<br>", "<empty-line\\/>");
        REPLACEMENTS.put("<a name=\\w+></a>", "");
        REPLACEMENTS.put("<h3 class=book>", "<\\/section><section><title>");
        REPLACEMENTS.put("<\\/h3>", "<\\/title>");
        REPLACEMENTS.put("<p class=book>", "<p>");
        REPLACEMENTS.put("<h5 class=book>", "<subtitle>");
        REPLACEMENTS.put("<\\/h5>", "<\\/subtitle>");
    }

    private final AuthorPage m_author;

    private final String m_genre;

    private final String m_sequence;

    private final String m_seqNo;

    private final String m_name;

    private final URL m_bookUrl;

    public BookPage(final AuthorPage author, final String name, final String genre, final String sequence, final String seqNo, final URL bookUrl) {
        super();
        m_author = author;
        m_name = name;
        m_genre = genre;
        m_sequence = sequence;
        m_seqNo = seqNo;
        m_bookUrl = bookUrl;
    }

    public AuthorPage getAuthor() {
        return m_author;
    }

    public URL getBookUrl() {
        return m_bookUrl;
    }

    public String getName() {
        return m_name;
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

    public XmlContent getContent() throws IOException {
        final URLConnection conn = m_bookUrl.openConnection();
        return getContent(conn.getInputStream(), "UTF8");
    }

    public XmlContent getContent(InputStream input, String encoding) throws UnsupportedEncodingException, IOException {
        final StringBuilder buf = new StringBuilder();

        final BufferedReader in = new BufferedReader(new InputStreamReader(input, encoding));

        boolean reachBook = false;
        boolean reachEnd = false;
        for (String s = in.readLine(); s != null && !reachEnd; s = in.readLine()) {
            if (!reachBook) {
                int ind = s.indexOf("<h3 class=book>");
                if (ind >= 0) {
                    s = s.substring(ind);
                    reachBook = true;
                }
            }
            if (reachBook) {
                int ind1 = s.indexOf("<!-- topadvert.ru -->");
                int ind2 = s.indexOf("<h3>");
                if (ind1 >= 0) {
                    s = s.substring(0, ind1);
                    reachEnd = true;
                } else if (ind2 >= 0) {
                    s = s.substring(0, ind2);
                    reachEnd = true;
                }
                buf.append(s).append('\n');
            }

        }

        String result = buf.toString();
        for (final Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }

        buf.setLength(0);
        buf.append(result);

        while (true) {
            int lastIndexOfDivEnd = buf.lastIndexOf("</div>");
            if (lastIndexOfDivEnd < 0) {
                break;
            }
            int nearestDiv = buf.lastIndexOf("<div", lastIndexOfDivEnd - 1);
            if (nearestDiv < 0) {
                break;
            }
            int pair = buf.indexOf("</div>", nearestDiv);
            if (buf.charAt(nearestDiv + 4) == '>') {
                buf.replace(pair, pair + "</div>".length(), "</v>");
                buf.replace(nearestDiv, nearestDiv + "<div>".length(), "<v>");
            } else {
                int classStart = buf.indexOf("=", nearestDiv);
                int classEnd = buf.indexOf(">", nearestDiv);
                String divClass = buf.substring(classStart + 1, classEnd).trim();
                if ("poem".equals(divClass)) {
                    buf.replace(pair, pair + "</div>".length(), "</poem>");
                    buf.replace(nearestDiv, classEnd + 1, "<poem>");
                } else if ("stanza".equals(divClass)) {
                    buf.replace(pair, pair + "</div>".length(), "</stanza>");
                    buf.replace(nearestDiv, classEnd + 1, "<stanza>");
                }
            }
        }

        result = buf.toString();

        String headerTemplate = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<FictionBook xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:l=\"http://www.w3.org/1999/xlink\">\n"
                + "<description><title-info>\n" + "<genre>{3}</genre>" + "<author> <first-name>{0}</first-name><last-name>{1}</last-name></author>"
                + "<book-title>{2}</book-title>\n" + "<date/><lang>ru</lang>"
                + (LengthUtils.isNotEmpty(this.m_sequence) ? "<sequence name=\"{4}\" number=\"{5}\"/>" : "") + "</title-info>\n" + "<document-info>"
                + "<author> <nickname>robot</nickname></author>" + "<program-used>LibRus.ec scanner robot</program-used>\n"
                + "<date value=\"{6,date,yyyy-MM-dd}\">{6,date,yyyy-MM-dd}</date>" + "<id></id>" + "<version>1.0</version>" + "</document-info>\n"
                + "</description>\n" + "<body>\n";

        Author author = new Author(m_author.getName(), null);
        String header = MessageFormat.format(headerTemplate, author.getFirstName(), author.getLastName(), m_name, m_genre, m_sequence, m_seqNo, new Date());

        buf.setLength(0);
        buf.append(header);

        int index = result.indexOf("</section>");
        if (index < 0) {
            buf.append(result);
        } else {
            buf.append(result, index + "</section>".length(), result.length());
            buf.append("</section>");
        }

        buf.append("</body></FictionBook>");

        System.out.println(buf);

        return new XmlContent(new ByteArrayInputStream(buf.toString().getBytes("UTF8")));
    }

    @Override
    public String toString() {
        return "BookPage [m_author=" + m_author + ", m_name=" + m_name + ", m_genre=" + m_genre + ", m_sequence=" + m_sequence + ", m_seqNo=" + m_seqNo
                + ", m_bookUrl=" + m_bookUrl + "]";
    }

}
