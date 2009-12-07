package org.ak2.fb2.library.book;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ak2.utils.XmlUtils;
import org.ak2.utils.streams.REFilterInputStream;
import org.ak2.utils.threadlocal.ThreadLocalPattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FictionBook {

    private static final ThreadLocalPattern ENCODING_PATTERN = new ThreadLocalPattern("encoding=\"([^\"]+)\"");

    private final Document fieldDocument;

    private String fieldBookName;

    private String fieldAuthor;

    public FictionBook(final InputStream inStream, final boolean filter) throws Exception {
        final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b;
        b = f.newDocumentBuilder();
        InputStream stream = inStream;
        if (filter) {
            final Map<String, String> replacements = new LinkedHashMap<String, String>();
            replacements.put("&", "&amp;");
            replacements.put("[\u0000-\u001f]", "");
            replacements.put("<<", "&lt;<");
            replacements.put("<([^a-zA-Z\\?\\/<])", "&lt;$1");
            replacements.put("([^a-zA-Z\\?\\\"\\/])>", "$1&gt;");
            stream = new REFilterInputStream(inStream, replacements);
        }
        fieldDocument = b.parse(stream);
    }

    public String getBookName() {
        if (fieldDocument == null) {
            return null;
        }
        if (fieldBookName != null) {
            return fieldBookName;
        }
        try {
            final Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/book-title");
            fieldBookName = element.getFirstChild().getNodeValue().trim();
            return fieldBookName;
        } catch (final Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    public void setBookName(final String bookName) {
        if (fieldDocument == null) {
            return;
        }
        fieldBookName = bookName.trim();
        try {
            final Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/book-title");
            element.getFirstChild().setNodeValue(fieldBookName);
        } catch (final Throwable th) {
            th.printStackTrace();
        }
    }

    public String getSequence() {
        try {
            final Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/sequence");
            final String fieldSeq = element.getAttribute("name");
            return fieldSeq.trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public void setSequence(final String seq) {
        try {
            Element seqElement = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/sequence");
            if (seqElement == null) {
                // Now we try to create sequence element, but we assume that title-info exist
                final Element titleInfo = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info");
                seqElement = fieldDocument.createElement("sequence");
                titleInfo.appendChild(seqElement);
            }
            seqElement.setAttribute("name", seq.trim());
        } catch (final Throwable th) {
        }
    }

    public String getSequenceNo() {
        try {
            final Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/sequence");
            final String fieldSeq = element.getAttribute("number");
            return fieldSeq.trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public void setSequenceNo(final String seqNo) {
        try {
            Element seqElement = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/sequence");
            if (seqElement == null) {
                // Now we try to create sequence element, but we assume that title-info exist
                final Element titleInfo = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info");
                seqElement = fieldDocument.createElement("sequence");
                titleInfo.appendChild(seqElement);
            }
            seqElement.setAttribute("number", seqNo.trim());
        } catch (final Throwable th) {
        }
    }

    public String getAuthor() {
        if (fieldDocument == null) {
            return null;
        }
        if (fieldAuthor == null) {
            try {
                fieldAuthor = (getAuthorLastName() + " " + getAuthorFirstName()).trim();
            } catch (final Throwable th) {
                th.printStackTrace();
            }
        }
        return fieldAuthor;
    }

    private String getAuthorFirstName() {
        try {
            final Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/author/first-name");
            final String fieldAuthorFirstName = element.getFirstChild().getNodeValue().trim();
            return fieldAuthorFirstName;
        } catch (final Throwable th) {
        }
        return "";
    }

    private String getAuthorLastName() {
        try {
            final Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/author/last-name");
            final String fieldAuthorLastName = element.getFirstChild().getNodeValue().trim();
            return fieldAuthorLastName;
        } catch (final Throwable th) {
        }
        return "";
    }

    public byte[] getBytes() throws TransformerFactoryConfigurationError, TransformerException {
        final CharArrayWriter buffer = new CharArrayWriter();
        final DOMSource source = new DOMSource(fieldDocument);
        final StreamResult result = new StreamResult(buffer);
        final TransformerFactory f = TransformerFactory.newInstance();
        final Transformer t = f.newTransformer();
        t.transform(source, result);
        return toBytes(buffer);
    }

    public InputStream getBookStream() throws TransformerFactoryConfigurationError, TransformerException {
        return new ByteArrayInputStream(getBytes());
    }

    private byte[] toBytes(final CharArrayWriter output) {
        final String text = output.toString();
        final String encoding = getXmlEncoding(text, getEncoding());
        try {
            return text.getBytes(encoding);
        } catch (final UnsupportedEncodingException ex) {
            return output.toString().getBytes();
        }
    }

    public String getEncoding() {
        return fieldDocument.getInputEncoding();
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
}
