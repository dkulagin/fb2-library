package org.ak2.fb2.library.book;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ak2.fb2.library.book.image.FictionBookImage;
import org.ak2.utils.XmlUtils;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FictionBook {

    private final Document fieldDocument;

    private final Element fieldTitleInfo;

    private final String fieldEncoding;

    private String fieldBookName;

    private String fieldAuthor;

    private Map<String, FictionBookImage> fieldImages;
    
    public FictionBook(final XmlContent content) throws Exception {
        fieldDocument = content.getDocument();
        fieldEncoding = content.getEncoding();
        fieldTitleInfo = (Element) XmlUtils.selectNode(fieldDocument, "/FictionBook/description/title-info");
    }

    public Document getDocument() {
        return fieldDocument;
    }
    public String getBookName() {
        if (fieldDocument == null) {
            return null;
        }
        if (fieldBookName == null) {
            try {
                fieldBookName = XmlUtils.getString(fieldTitleInfo, "book-title").trim();
            } catch (final Throwable th) {
            }
        }
        return fieldBookName;
    }

    public void setBookName(final String bookName) {
        if (fieldDocument == null) {
            return;
        }
        fieldBookName = bookName.trim();
        try {
            Element element = (Element) XmlUtils.selectNode(fieldTitleInfo, "book-title");
            if (element == null) {
                element = createElement(fieldTitleInfo, "book-title");
            }
            element.setTextContent(fieldBookName);
        } catch (final Throwable th) {
            th.printStackTrace();
        }
    }

    public String getSequence() {
        if (fieldDocument == null) {
            return null;
        }
        try {
            return XmlUtils.getString(fieldTitleInfo, "sequence/@name").trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public void setSequence(final String seq) {
        if (fieldDocument == null) {
            return;
        }
        try {
            Element seqElement = (Element) XmlUtils.selectNode(fieldTitleInfo, "sequence");
            if (seqElement == null) {
                seqElement = createElement(fieldTitleInfo, "sequence");
            }
            seqElement.setAttribute("name", seq.trim());
        } catch (final Throwable th) {
        }
    }

    public String getSequenceNo() {
        if (fieldDocument == null) {
            return null;
        }
        try {
            return XmlUtils.getString(fieldTitleInfo, "sequence/@number").trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public void setSequenceNo(final String seqNo) {
        if (fieldDocument == null) {
            return;
        }
        try {
            Element seqElement = (Element) XmlUtils.selectNode(fieldTitleInfo, "sequence");
            if (seqElement == null) {
                seqElement = createElement(fieldTitleInfo, "sequence");
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
                fieldAuthor = (getAuthorLastName() + " " + getAuthorFirstName());
            } catch (final Throwable th) {
            }
        }
        return fieldAuthor;
    }

    public String getAuthorFirstName() {
        if (fieldDocument == null) {
            return null;
        }
        try {
            return XmlUtils.getString(fieldTitleInfo, "author/first-name").trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public void setAuthorFirstName(String firstName) {
        if (fieldDocument == null) {
            return;
        }
        Element element = (Element) XmlUtils.selectNode(fieldTitleInfo, "author/first-name");
        if (element == null) {
            element = (Element) XmlUtils.selectNode(fieldTitleInfo, "author");
            if (element == null) {
                element = createElement(fieldTitleInfo, "author");
            }
            element = createElement(element, "first-name");
        }
        element.setTextContent(firstName);
    }

    public String getAuthorLastName() {
        try {
            return XmlUtils.getString(fieldTitleInfo, "author/last-name").trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public void setAuthorLastName(String lastName) {
        if (fieldDocument == null) {
            return;
        }
        Element element = (Element) XmlUtils.selectNode(fieldTitleInfo, "author/last-name");
        if (element == null) {
            element = (Element) XmlUtils.selectNode(fieldTitleInfo, "author");
            if (element == null) {
                element = createElement(fieldTitleInfo, "author");
            }
            element = createElement(element, "last-name");
        }
        element.setTextContent(lastName);
    }

    public int getImageIndex(final String imageFileName) {
        FictionBookImage[] images = getImages();
        for (int i = 0; i < images.length; i++) {
            FictionBookImage image = images[i];
            if (image.getImageFileName().equals(imageFileName)) {
                return i;
            }
        }
        return -1;
    }
    
    public FictionBookImage getImage(final String imageFileName) {
        return getImageMap().get(imageFileName);
    }
    
    public String[] getImageFileNames() {
        Map<String, FictionBookImage> imageMap = getImageMap();
        return imageMap.keySet().toArray(new String[imageMap.size()]);
    }

    public FictionBookImage[] getImages() {
        Map<String, FictionBookImage> imageMap = getImageMap();
        return imageMap.values().toArray(new FictionBookImage[imageMap.size()]);
    }
    
    protected Map<String, FictionBookImage> getImageMap() {
        if (fieldImages == null) {
            fieldImages = new HashMap<String, FictionBookImage>();
            if (fieldDocument != null) {
                int index = 0;
                for (Node node : XmlUtils.selectNodes(fieldDocument, "/FictionBook/binary")) {
                    Element element = (Element) node;
                    try {
                    FictionBookImage image = new FictionBookImage(element);
                    fieldImages.put(image.getImageFileName(), image);
                    index++;
                    } catch (Throwable th) {
                        new JLogMessage(JLogLevel.ERROR, "Image {0} cannot be loaded: ").log(th, index);
                    }
                }
            }
        }
        return fieldImages;
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

    private byte[] toBytes(final CharArrayWriter output) {
        final String text = output.toString();
        try {
            return text.getBytes(fieldEncoding);
        } catch (final UnsupportedEncodingException ex) {
            return text.getBytes();
        }
    }

    private Element createElement(Element parent, String... tagNames) {
        Element e = parent;
        for (String tagName : tagNames) {
            e = (Element) e.appendChild(fieldDocument.createElement(tagName));
        }
        return e;
    }

}
