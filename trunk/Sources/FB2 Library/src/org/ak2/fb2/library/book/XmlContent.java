package org.ak2.fb2.library.book;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.xml.parsers.ParserConfigurationException;

import org.ak2.fb2.library.common.Encoding;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.IFile;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.ak2.utils.threadlocal.ThreadLocalDocumentBuilder;
import org.ak2.utils.threadlocal.ThreadLocalPattern;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlContent {

    private static final JLogMessage MSG_PARSER_ERROR = new JLogMessage(JLogLevel.ERROR, "Xml parser error: {0}");

    private static final ThreadLocalPattern ENCODING_PATTERN = new ThreadLocalPattern("encoding=\"([^\"]+)\"");

    private static final Map<String, String> REPLACEMENTS = new LinkedHashMap<String, String>();

    static {
        REPLACEMENTS.put("&", "&amp;");
        REPLACEMENTS.put("[\u0000-\u0009\u000B-\u000C\u000E-\u001f]", "");
        REPLACEMENTS.put("<<", "&lt;<");
        REPLACEMENTS.put("<([^a-zA-Z\\?\\/<])", "&lt;$1");
        REPLACEMENTS.put("([^a-zA-Z\\?\\\"\\/])>", "$1&gt;");
    }

    private final byte[] original;
    private final Encoding realEncoding;
    private final String xmlEncoding;

    private WeakReference<String> content;
    private WeakReference<Document> document;

    public XmlContent(final IFile file) throws IOException {
        this(file.open());
    }

    public XmlContent(final InputStream in) throws IOException {
        original = loadContent(in);
        realEncoding = getRealEncoding(new ByteArrayInputStream(original));
        xmlEncoding = getXmlEncoding(original, null);
    }

    public byte[] getOriginal() {
        return original;
    }

    public Encoding getRealEncoding() {
        return realEncoding;
    }

    public String getXmlEncoding() {
        return xmlEncoding;
    }

    public String getEncoding() {
        return realEncoding != null ? realEncoding.name() : xmlEncoding != null ? xmlEncoding : "UTF8";
    }

    public boolean isWrongEncoding() {
        if (realEncoding == null) {
            return false;
        }
        final String newEncoding = realEncoding.getXmlName();
        final String oldEncoding = getXmlEncoding();
        return !LengthUtils.equalsIgnoreCase(oldEncoding, newEncoding);
    }

    public String getContent() throws IOException {
        String str = content != null ? content.get() : null;
        if (str == null) {
            str = new String(getOriginal(), getEncoding());
            if (isWrongEncoding()) {
                final StringBuilder buf = new StringBuilder(str);
                str = fixXmlEncoding(buf, getRealEncoding().getXmlName()).toString();
            }
            content = new WeakReference<String>(str);
        }
        return str;
    }

    public byte[] getContentAsBytes() throws IOException {
        return getContent().getBytes(getEncoding());
    }

    public Document getDocument() throws IOException, ParserConfigurationException, SAXException {
        Document doc = document != null ? document.get() : null;
        if (doc == null) {
            String str = getContent();
            try {
                doc = ThreadLocalDocumentBuilder.parse(new InputSource(new StringReader(str)));
            } catch (final SAXException ex) {
                String msg = ex.getMessage();
                MSG_PARSER_ERROR.log(msg);
                str = fixXmlContent(str);
                doc = ThreadLocalDocumentBuilder.parse(new InputSource(new StringReader(str)));
            }
        }
        return doc;
    }

    public static byte[] loadContent(final InputStream in) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[64 * 1024];
        try {
            for (int len = in.read(buffer); len != -1; len = in.read(buffer)) {
                baos.write(buffer, 0, len);
            }
        } finally {
            in.close();
        }
        return baos.toByteArray();
    }

    public static Encoding getRealEncoding(final InputStream inStream) throws IOException {
        final int utf8Length = Encoding.UTF8.getPattern().length;
        final int winLength = Encoding.CP1251.getPattern().length;
        final int[] buf = new int[Math.max(utf8Length, winLength)];

        int head = 0;
        int tail = 0;

        for (int val = inStream.read(); val != -1; val = inStream.read()) {
            buf[tail] = val;
            tail = (tail + 1) % buf.length;
            final int bufLength = (buf.length + tail - head) % buf.length;

            for (final Encoding enc : Encoding.values()) {
                final int[] pattern = enc.getPattern();
                final int encLength = pattern.length;
                if (bufLength >= encLength) {
                    boolean exact = true;
                    for (int i = 0; i < encLength && exact; i++) {
                        exact = pattern[i] == buf[head + i];
                    }
                    if (exact) {
                        return enc;
                    }
                }
            }
            if (bufLength == buf.length) {
                head = (head + 1) % buf.length;
            }
        }
        return null;
    }

    public static String getXmlEncoding(final byte[] bytes, final String defaultEncoding) {
        String text = new String(bytes, 0, Math.min(bytes.length, 1024));
        final Matcher m = ENCODING_PATTERN.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return defaultEncoding;
    }

    public static String getXmlEncoding(final CharSequence text, final String defaultEncoding) {
        final Matcher m = ENCODING_PATTERN.matcher(text.subSequence(0, 1024));
        if (m.find()) {
            return m.group(1);
        }
        return defaultEncoding;
    }

    public static StringBuilder fixXmlEncoding(final StringBuilder text, final String encoding) {
        final Matcher m = ENCODING_PATTERN.matcher(text.subSequence(0, 1024));
        if (m.find()) {
            final int start = m.start(1);
            final int end = m.end(1);
            text.delete(start, end);
            text.insert(start, encoding);
        }
        return text;
    }

    public static String fixXmlContent(final String content) {
        String result = content;
        for (final Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
