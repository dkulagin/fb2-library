package org.ak2.fb2.library.book;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.ak2.utils.threadlocal.ThreadLocalPattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FictionBook {

    private static final ThreadLocalPattern ENCODING_PATTERN = new ThreadLocalPattern("encoding=\"([^\"]+)\"");

    private Document fieldDocument;

    private String fieldBookName;

    private String fieldAuthor;

    public FictionBook(InputStream inStream) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b;
        b = f.newDocumentBuilder();
        fieldDocument = b.parse(inStream);
    }

    public String getBookName() {
        if (fieldDocument == null)
            return null;

        if (fieldBookName != null)
            return fieldBookName;

        try {
            Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/book-title");
            fieldBookName = element.getFirstChild().getNodeValue().trim();
            return fieldBookName;

        } catch (Throwable th) {
            th.printStackTrace();
        }

        return null;
    }

    public String getAuthor() {
        if (fieldDocument == null)
            return null;

        if (fieldAuthor == null) {
            try {
                fieldAuthor = (getAuthorLastName() + " " + getAuthorFirstName()).trim();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return fieldAuthor;
    }

    private String getAuthorFirstName() {
        try {
            Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/author/first-name");
            String fieldAuthorFirstName = element.getFirstChild().getNodeValue().trim();
            return fieldAuthorFirstName;

        } catch (Throwable th) {
        }
        return "";
    }

    private String getAuthorLastName() {
        try {
            Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/author/last-name");
            String fieldAuthorLastName = element.getFirstChild().getNodeValue().trim();

            return fieldAuthorLastName;

        } catch (Throwable th) {
        }

        return "";
    }

    public byte[] getBytes() throws TransformerFactoryConfigurationError, TransformerException {
        CharArrayWriter buffer = new CharArrayWriter();

        DOMSource source = new DOMSource(fieldDocument);
        StreamResult result = new StreamResult(buffer);

        TransformerFactory f = TransformerFactory.newInstance();
        Transformer t = f.newTransformer();
        t.transform(source, result);

        return toBytes(buffer);
    }

    public InputStream getBookStream() throws TransformerFactoryConfigurationError, TransformerException {
        return new ByteArrayInputStream(getBytes());
    }

    private byte[] toBytes(CharArrayWriter output) {
        String text = output.toString();
        String encoding = getXmlEncoding(text, getEncoding());
        try {
            return text.getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            return output.toString().getBytes();
        }
    }

    public String getSequence() {
        try {
            Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/sequence");
            String fieldSeq = element.getAttribute("name");

            return fieldSeq.trim();

        } catch (Throwable th) {
        }

        return "";
    }

    public String getSequenceNo() {
        try {
            Element element = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info/sequence");
            String fieldSeq = element.getAttribute("number");

            return fieldSeq.trim();

        } catch (Throwable th) {
        }

        return "";
    }

    public String getEncoding() {
        return fieldDocument.getInputEncoding();
    }

    public static String getXmlEncoding(CharSequence text, String defaultEncoding) {
        Matcher m = ENCODING_PATTERN.matcher(text.subSequence(0, 1024));
        if (m.find()) {
            return m.group(1);
        }
        return defaultEncoding;
    }

    public static StringBuilder fixXmlEncoding(StringBuilder text, String encoding) {
        Matcher m = ENCODING_PATTERN.matcher(text.subSequence(0, 1024));
        if (m.find()) {
            int start = m.start(1);
            int end = m.end(1);
            text.delete(start, end);
            text.insert(start, encoding);
        }
        return text;
    }
}
