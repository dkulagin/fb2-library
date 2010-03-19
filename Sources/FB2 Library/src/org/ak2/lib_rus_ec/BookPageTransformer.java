package org.ak2.lib_rus_ec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
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

public class BookPageTransformer {

    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<String, String>();

    static {
        REPLACEMENTS.put("<i>", "<emphasis>");
        REPLACEMENTS.put("</i>", "</emphasis>");
        REPLACEMENTS.put("<b>", "<strong>");
        REPLACEMENTS.put("</b>", "</strong>");
        REPLACEMENTS.put("<br>", "<empty-line\\/>");
        REPLACEMENTS.put("<a name=\\w+></a>", "");
        REPLACEMENTS.put("<p class=book>", "<p>");
        REPLACEMENTS.put("<p><p>", "<p>");
        REPLACEMENTS.put("<h5 class=book>", "<subtitle>");
        REPLACEMENTS.put("<\\/h5>", "<\\/subtitle>");
        REPLACEMENTS.put("<SUP>", "<sup>");
        REPLACEMENTS.put("</SUP>", "</sup>");
        REPLACEMENTS.put("<SUB>", "<sub>");
        REPLACEMENTS.put("</SUB>", "</sub>");
    }

    private BookPage m_bookPage;

    private boolean m_fixNotes = true;

    private boolean m_fixTitles = true;

    private boolean m_fixDivs = true;

    private boolean m_fixBlockquotes = true;

    private boolean m_fixImages = true;

    private boolean m_fixTags = true;

    private boolean m_fixTitleParagraphs = true;

    private boolean m_fixSections = true;

    private boolean m_loadImages = true;

    public BookPageTransformer() {
    }

    public BookPage getBookPage() {
        return m_bookPage;
    }

    /**
     * Phase 1.
     *
     * @param fixNotes
     *            transformation flag
     */
    public void setFixNotes(boolean fixNotes) {
        m_fixNotes = fixNotes;
    }

    /**
     * Phase 2.
     *
     * @param fixTitles
     *            transformation flag
     */
    public void setFixTitles(boolean fixTitles) {
        m_fixTitles = fixTitles;
    }

    /**
     * Phase 3.
     *
     * @param fixDivs
     *            transformation flag
     */
    public void setFixDivs(boolean fixDivs) {
        m_fixDivs = fixDivs;
    }

    /**
     * Phase 4.
     *
     * @param fixBlockquotes
     *            transformation flag
     */
    public void setFixBlockquotes(boolean fixBlockquotes) {
        m_fixBlockquotes = fixBlockquotes;
    }

    /**
     * Phase 5.
     *
     * @param fixImages
     *            transformation flag
     */
    public void setFixImages(boolean fixImages) {
        m_fixImages = fixImages;
    }

    /**
     * Phase 6.
     *
     * @param fixTags
     *            transformation flag
     */
    public void setFixTags(boolean fixTags) {
        m_fixTags = fixTags;
    }

    /**
     * Phase 7.
     *
     * @param fixTitleParagraphs
     *            transformation flag
     */
    public void setFixTitleParagraphs(boolean fixTitleParagraphs) {
        m_fixTitleParagraphs = fixTitleParagraphs;
    }

    /**
     * Phase 8.
     *
     * @param fixSections
     *            transformation flag
     */
    public void setFixSections(boolean fixSections) {
        m_fixSections = fixSections;
    }

    /**
     * Phase 9.
     *
     * @param loadImages
     *            transformation flag
     */
    public void setLoadImages(boolean loadImages) {
        m_loadImages = loadImages;
    }

    public XmlContent transform(final BookPage bookPage, final StringBuilder buf) throws IOException {
        m_bookPage = bookPage;

        if (m_fixNotes) {
            fixNotes(buf);
        }

        if (m_fixTitles) {
            fixTitles(buf);
        }

        if (m_fixDivs) {
            fixDiv(buf);
        }

        if (m_fixBlockquotes) {
            fixBlockquote(buf);
        }

        if (m_fixImages) {
            m_bookPage.setImages(fixImages(buf));
        }

        if (m_fixTags) {
            fixTags(buf);
        }

        if (m_fixTitleParagraphs) {
            fixTitleParagraphs(buf);
        }

        if (m_fixSections) {
            fixSection(buf);
        }

        makeHeaderFooter(buf);

        if (m_loadImages) {
            loadImages(buf);
        }

        buf.append("</FictionBook>");

        return new XmlContent(buf.toString());
    }

    void fixNotes(final StringBuilder buf) {

        // <a l:href="#n01" type="note">[1]</a>
        // <sup><a name=r1><a href="#n1" title="Подробнее — см. дополнительную главу 1-а.">[1]</sup></A>

        final Pattern p = Pattern
                .compile("<sup><a name=\\w+><a href=\"#(\\w+)\" title=\"[^\"]+\">([^<]+)</sup></A>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

        int start = 0;
        for (Matcher m = p.matcher(buf); m.find(start); m = p.matcher(buf)) {
            final String id = m.group(1);
            final String text = m.group(2);
            start = m.start();
            final int end = m.end();
            buf.replace(start, end, "<a l:href=\"#" + id + "\" type=\"note\">" + text + "</a>");
        }

        // <h3 class=book>
        // <p class=book>Примечания
        // </h3>
        // <a name="n_1"></a><h3 class=book>
        // <p class=book>1
        // </h3>
        // <p class=book><p class=book>Вернемся к нашим баранам…</p>
        // <small>(<a href=#r1>обратно</a>)</small>

        final Pattern pt = Pattern
                .compile(
                        "(<h3 class=book>\\s*(?:<p class=book>)?([^<]*?)(:?</p>)?\\s*</h3>)\\s*<a name=\\\"\\w+\\\">\\s*</a>\\s*<h3 class=book>.*?</h3>\\s*<p class=book>.*?</p>\\s*<small>\\(<a href=#\\w+>.+?</a>\\)</small>",
                        Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
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
                    "<a name=\\\"(\\w+)\\\">\\s*</a>\\s*<h3 class=book>\\s*(?:<p class=book>)?(.*?)\\s*</h3>\\s*(?:<p class=book>)+(.*?)</p>\\s*<small>\\(<a href=#\\w+>.+?</a>\\)</small>",
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
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

    void fixTitles(final StringBuilder buf) {
        int stage = fixTitle(buf, 0);
        while (stage != -1) {
            stage = fixTitle(buf, stage);
        }
    }

    private int fixTitle(final StringBuilder buf, final int stage) {
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

    void fixDiv(final StringBuilder buf) {
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

    void fixBlockquote(final StringBuilder buf) {
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
            final BookImage image = new BookImage(m_bookPage, link);
            images.add(image);

            start = m.start();
            final int end = m.end();

            buf.replace(start, end, "<image l:href=\"#" + image.getId() + "\"/>");
        }

        return images;
    }

    void fixTags(final StringBuilder buf) {
        String result = buf.toString();
        for (final Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        buf.setLength(0);
        buf.append(result);
    }

    void fixTitleParagraphs(final StringBuilder buf) {
        int start = 0;
        while (true) {
            start = buf.indexOf("<title>", start);
            if (start < 0) {
                return;
            }
            start += "<title>".length();

            int end = buf.indexOf("</title>", start);
            if (end < 0) {
                end = buf.length();
            }

            StringBuilder content = new StringBuilder(buf.substring(start, end));
            int pStart = 0;
            while (true) {
                pStart = content.indexOf("<p>", pStart);
                if (pStart < 0) {
                    break;
                }
                pStart += "<p>".length();

                int pEnd = content.indexOf("</p>", pStart);
                int pNextP = content.indexOf("<p>", pStart);
                if (pNextP < 0) {
                    pNextP = content.length();
                }
                if (pEnd < 0 || pEnd > pNextP) {
                    while (pNextP >= 0) {
                        char charAt = content.charAt(pNextP - 1);
                        if ((charAt != '\n' && charAt != '\r')) {
                            break;
                        }
                        pNextP--;
                    }
                    content.insert(pNextP, "</p>");
                }
            }

            buf.replace(start, end, content.toString());
        }
    }

    void fixSection(final StringBuilder buf) {
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

    void makeHeaderFooter(final StringBuilder buf) {
        final String headerTemplate = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<FictionBook xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:l=\"http://www.w3.org/1999/xlink\">\n"
                + "<description><title-info>\n" + "<genre>{3}</genre>" + "<author> <first-name>{0}</first-name><last-name>{1}</last-name></author>"
                + "<book-title>{2}</book-title>\n" + "<date/><lang>ru</lang>"
                + (LengthUtils.isNotEmpty(m_bookPage.getSequence()) ? "<sequence name=\"{4}\" number=\"{5}\"/>" : "") + "</title-info>\n" + "<document-info>"
                + "<author> <nickname>robot</nickname></author>" + "<program-used>LibRus.ec scanner robot</program-used>\n"
                + "<date value=\"{6,date,yyyy-MM-dd}\">{6,date,yyyy-MM-dd}</date>" + "<id></id>" + "<version>1.0</version>" + "</document-info>\n"
                + "</description>\n" + "<body>\n";

        final BookAuthor author = m_bookPage.getAuthorPage().getAuthor();
        final String header = MessageFormat.format(headerTemplate, author.getFirstName(), author.getLastName(), m_bookPage.getName(), m_bookPage.getGenre(),
                m_bookPage.getSequence(), m_bookPage.getSeqNo(), new Date());

        buf.insert(0, header);
        buf.append("</body>");
    }

    void loadImages(final StringBuilder buf) throws IOException {
        for (final BookImage image : m_bookPage.getImages()) {
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
