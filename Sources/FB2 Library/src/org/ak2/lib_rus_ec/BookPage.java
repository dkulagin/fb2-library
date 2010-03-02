package org.ak2.lib_rus_ec;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.library.book.XmlContent;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.base64.Base64;

public class BookPage {

    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<String, String>();

    static {
        REPLACEMENTS.put("<i>", "<emphasis>");
        REPLACEMENTS.put("</i>", "</emphasis>");
        REPLACEMENTS.put("<b>", "<strong>");
        REPLACEMENTS.put("</b>", "</strong>");
        REPLACEMENTS.put("<br>", "<empty-line\\/>");
        REPLACEMENTS.put("<a name=\\w+></a>", "");
        REPLACEMENTS.put("<p class=book>", "<p>");
        REPLACEMENTS.put("<h5 class=book>", "<subtitle>");
        REPLACEMENTS.put("<\\/h5>", "<\\/subtitle>");
        REPLACEMENTS.put("<SUP>", "<sup>");
        REPLACEMENTS.put("</SUP>", "</sup>");
        REPLACEMENTS.put("<SUB>", "<sub>");
        REPLACEMENTS.put("</SUB>", "</sub>");
    }

    private final AuthorPage m_authorPage;

    private final String m_genre;

    private final String m_sequence;

    private final String m_seqNo;

    private final String m_name;

    private final String m_link;

    private Set<BookImage> m_images = Collections.emptySet();

    public BookPage(final AuthorPage authorPage, final String name, final String genre, final String sequence, final String seqNo, final String link) {
        super();
        m_authorPage = authorPage;
        m_name = name;
        m_genre = genre;
        m_sequence = sequence;
        m_seqNo = seqNo;
        m_link = link;
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

    public XmlContent getContent() throws IOException {
        final URL authorUrl = m_authorPage != null ? m_authorPage.getAuthorUrl() : null;
        if (authorUrl == null) {
            throw new IllegalArgumentException("Book author URL unknown");
        }
        final URL url = new URL(authorUrl.getProtocol(), authorUrl.getHost(), m_link + "/read");
        final URLConnection conn = url.openConnection();
        return getContent(conn.getInputStream(), "UTF8");
    }

    public XmlContent getContent(final InputStream input, final String encoding) throws UnsupportedEncodingException, IOException {
        final StringBuilder buf = loadText(input, encoding);

        fixNotes(buf);
        fixTitles(buf);
        fixDiv(buf);
        fixBlockquote(buf);

        m_images = fixImages(buf);

        fixTags(buf);

        fixSection(buf);

        final String headerTemplate = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<FictionBook xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:l=\"http://www.w3.org/1999/xlink\">\n"
                + "<description><title-info>\n" + "<genre>{3}</genre>" + "<author> <first-name>{0}</first-name><last-name>{1}</last-name></author>"
                + "<book-title>{2}</book-title>\n" + "<date/><lang>ru</lang>"
                + (LengthUtils.isNotEmpty(this.m_sequence) ? "<sequence name=\"{4}\" number=\"{5}\"/>" : "") + "</title-info>\n" + "<document-info>"
                + "<author> <nickname>robot</nickname></author>" + "<program-used>LibRus.ec scanner robot</program-used>\n"
                + "<date value=\"{6,date,yyyy-MM-dd}\">{6,date,yyyy-MM-dd}</date>" + "<id></id>" + "<version>1.0</version>" + "</document-info>\n"
                + "</description>\n" + "<body>\n";

        final BookAuthor author = m_authorPage.getAuthor();
        final String header = MessageFormat.format(headerTemplate, author.getFirstName(), author.getLastName(), m_name, m_genre, m_sequence, m_seqNo,
                new Date());

        buf.insert(0, header);
        buf.append("</body>");

        loadImages(buf);

        buf.append("</FictionBook>");

        return new XmlContent(new ByteArrayInputStream(buf.toString().getBytes("UTF8")));
    }

    @Override
    public String toString() {
        return "BookPage [m_authorPage=" + m_authorPage + ", m_name=" + m_name + ", m_genre=" + m_genre + ", m_sequence=" + m_sequence + ", m_seqNo=" + m_seqNo
                + ", m_link=" + m_link + "]";
    }

    static StringBuilder loadText(final InputStream input, final String encoding) throws UnsupportedEncodingException, IOException {
        final StringBuilder buf = new StringBuilder();

        final BufferedReader in = new BufferedReader(new InputStreamReader(input, encoding));

        boolean reachBook = false;
        boolean reachEnd = false;
        for (String s = in.readLine(); s != null && !reachEnd; s = in.readLine()) {
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
                buf.append(s).append('\n');
            }

        }
        return buf;
    }

    static void fixNotes(final StringBuilder buf) {

        // <a l:href="#n01" type="note">[1]</a>
        // <sup><a name=r1><a href="#n1" title="Подробнее — см. дополнительную главу 1-а.">[1]</sup></A>

        final Pattern p = Pattern.compile("<sup><a name=\\w+><a href=\"#(\\w+)\" title=\"[^\"]+\">([^<]+)</sup></A>", Pattern.DOTALL);

        int start = 0;
        for (Matcher m = p.matcher(buf); m.find(start); m = p.matcher(buf)) {
            final String id = m.group(1);
            final String text = m.group(2);
            start = m.start();
            final int end = m.end();
            buf.replace(start, end, "<a l:href=\"#" + id + "\" type=\"note\">" + text + "</a>");
        }

        // <h3 class=book>
        // Примечания
        // </h3>
        // <a name="n1"></a>
        // <h3 class=book>

        final Pattern pt = Pattern
                .compile(
                        "(<h3 class=book>([^<]+)</h3>)\\s*<a name=\\\"\\w+\\\">\\s*</a>\\s*<h3 class=book>.*?</h3>\\s*<p class=book>.*?</p>\\s*<small>\\(<a href=#\\w+>.+?</a>\\)</small>",
                        Pattern.DOTALL);
        final Matcher m = pt.matcher(buf);
        if (m.find()) {
            start = m.start(1);
            int end = m.end(1);
            final String title = m.group(2).trim();

            // </section>
            // </body>
            // <body name="notes">
            // <title>
            // <p>Примечания</p>
            // </title>

            buf.replace(start, end, "\n</section></body>\n<body name=\"notes\">\n<title><p>" + title + "</p></title>\n");

            // <a name="n1"></a>
            // <h3 class=book>
            // 1.
            // </h3>
            // <p>Подробнее — см. дополнительную главу 1-а.</p>
            // <small>(<a href=#r1>обратно</a>)</small>

            final Pattern pn = Pattern.compile(
                    "<a name=\\\"(\\w+)\\\">\\s*</a>\\s*<h3 class=book>(.*?)</h3>\\s*<p class=book>(.*?)</p>\\s*<small>\\(<a href=#\\w+>.+?</a>\\)</small>",
                    Pattern.DOTALL);
            for (Matcher mn = pn.matcher(buf); mn.find(start); mn = pn.matcher(buf)) {
                final String id = mn.group(1);
                final String note = mn.group(2).trim();
                final String text = mn.group(3).trim();
                start = mn.start();
                end = mn.end();

                // <section id="n1">
                // <title>
                // <p>1.</p>
                // </title>
                // <p>Подробнее — см. дополнительную главу 1-а.</p>
                // </section>

                buf.replace(start, end, "<section id=\"" + id + "\"><title><p>" + note + "</p></title><p>" + text + "</p></section>\n");
            }
        }
    }

    static void fixTitles(final StringBuilder buf) {
        int stage = fixTitle(buf, 0);
        while (stage != -1) {
            stage = fixTitle(buf, stage);
        }
    }

    static int fixTitle(final StringBuilder buf, final int stage) {
        final String headerStart = "<h3 class=book>";
        final String headerEnd = "</h3>";

        final int startIndex = buf.indexOf(headerStart);
        if (startIndex < 0) {
            return -1;
        }
        final int endIndex = buf.indexOf(headerEnd, startIndex + headerStart.length());
        if (endIndex < 0) {
            return -1;
        }

        switch (stage) {
        case 0:
            buf.replace(endIndex, endIndex + headerEnd.length(), "</title>");
            buf.replace(startIndex, startIndex + headerStart.length(), "<title>");
            break;
        case 1:
            buf.replace(endIndex, endIndex + headerEnd.length(), "</title>");
            buf.replace(startIndex, startIndex + headerStart.length(), "<section><title>");
            break;
        default:
            buf.replace(endIndex, endIndex + headerEnd.length(), "</title>");
            buf.replace(startIndex, startIndex + headerStart.length(), "</section><section><title>");
            break;
        }

        return stage + 1;
    }

    static void fixDiv(final StringBuilder buf) {
        while (true) {
            final int lastIndexOfDivEnd = buf.lastIndexOf("</div>");
            if (lastIndexOfDivEnd < 0) {
                break;
            }
            final int nearestDiv = buf.lastIndexOf("<div", lastIndexOfDivEnd - 1);
            if (nearestDiv < 0) {
                break;
            }
            final int pair = buf.indexOf("</div>", nearestDiv);
            if (buf.charAt(nearestDiv + 4) == '>') {
                buf.replace(pair, pair + "</div>".length(), "</v>");
                buf.replace(nearestDiv, nearestDiv + "<div>".length(), "<v>");
            } else {
                final int classStart = buf.indexOf("=", nearestDiv);
                final int classEnd = buf.indexOf(">", nearestDiv);
                final String divClass = buf.substring(classStart + 1, classEnd).trim();
                if ("poem".equals(divClass)) {
                    buf.replace(pair, pair + "</div>".length(), "</poem>");
                    buf.replace(nearestDiv, classEnd + 1, "<poem>");
                } else if ("stanza".equals(divClass)) {
                    buf.replace(pair, pair + "</div>".length(), "</stanza>");
                    buf.replace(nearestDiv, classEnd + 1, "<stanza>");
                }
            }
        }
    }

    static void fixBlockquote(final StringBuilder buf) {
        final String blockQuote = "<blockquote";
        final String blockQuoteEnd = "</blockquote>";

        int firstIndex = 0;
        while (true) {
            firstIndex = buf.indexOf(blockQuote, firstIndex);
            if (firstIndex < 0) {
                break;
            }
            final int nearestEnd = buf.indexOf(blockQuoteEnd, firstIndex + 1);
            if (nearestEnd < 0) {
                break;
            }

            final int pair = buf.lastIndexOf(blockQuote, nearestEnd - blockQuoteEnd.length());

            if (buf.charAt(pair + blockQuote.length()) == '>') {
            } else {
                final int classStart = buf.indexOf("=", pair);
                final int classEnd = buf.indexOf(">", pair);
                final String quoteClass = buf.substring(classStart + 1, classEnd).trim();

                if ("\"epigraph\"".equals(quoteClass)) {
                    buf.replace(nearestEnd, nearestEnd + blockQuoteEnd.length(), "</cite>");
                    buf.replace(pair, classEnd + 1, "<cite>");
                } else if ("book".equals(quoteClass)) {
                    buf.replace(nearestEnd, nearestEnd + blockQuoteEnd.length(), "</text-author>");
                    buf.replace(pair, classEnd + 1, "<text-author>");
                }
            }
        }
    }

    Set<BookImage> fixImages(final StringBuilder buf) throws MalformedURLException {
        final Set<BookImage> images = new LinkedHashSet<BookImage>();

        // <img border=0 style='spacing 9px;' src="/i/56/146556/yes01.png">
        // <image l:href="#img01.png"/>

        final Pattern p = Pattern.compile("<img (?:border=0 style='spacing 9px;')? src=\"(/i/[^\"]+)\">", Pattern.DOTALL);

        int start = 0;
        for (Matcher m = p.matcher(buf); m.find(start); m = p.matcher(buf)) {
            final String link = m.group(1);
            final BookImage image = new BookImage(this, link);
            images.add(image);

            start = m.start();
            final int end = m.end();

            buf.replace(start, end, "<image l:href=\"#" + image.getId() + "\"/>");
        }

        return images;
    }

    static void fixTags(final StringBuilder buf) {
        String result = buf.toString();
        for (final Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        buf.setLength(0);
        buf.append(result);
    }

    static void fixSection(final StringBuilder buf) {
        final String sectionStart = "<section";
        final String sectionEnd = "</section>";

        final int startIndex = buf.lastIndexOf(sectionStart);
        if (startIndex < 0) {
            return;
        }

        final int endIndex = buf.lastIndexOf(sectionEnd);
        if (startIndex > endIndex) {
            buf.append(sectionEnd);
        }
    }

    void loadImages(final StringBuilder buf) throws IOException {
        for (final BookImage image : m_images) {
            final byte[] content = image.getContent();
            final String base64 = Base64.encodeBytes(content);

            // <binary content-type="image/png" id="skull.png">
            // ...
            // </binary>

            buf.append("<binary content-type=\"" + image.getContentType() + "\" id=\"" + image.getId() + "\">\n");
            buf.append(base64);
            if (!base64.endsWith("\n")) {
                buf.append("\n");
            }
            buf.append("</binary>\n");
        }
    }

}
