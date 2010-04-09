package org.ak2.fb2.library.book;

import org.ak2.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FictionBookInfo {

    private final Document fieldDocument;

    private final Element fieldTitleInfo;

    private String fieldBookName;

    private BookAuthor fieldAuthor;

    public FictionBookInfo(final Node root) throws Exception {
        fieldDocument = root.getOwnerDocument();
        fieldTitleInfo = (Element) XmlUtils.selectNode(root, "title-info");
    }

    public final String getBookName() {
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

    public final void setBookName(final String bookName) {
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

    public final String getSequence() {
        if (fieldDocument == null) {
            return null;
        }
        try {
            return XmlUtils.getString(fieldTitleInfo, "sequence/@name").trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public final void setSequence(final String seq) {
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

    public final String getSequenceNo() {
        if (fieldDocument == null) {
            return null;
        }
        try {
            return XmlUtils.getString(fieldTitleInfo, "sequence/@number").trim();
        } catch (final Throwable th) {
        }
        return "";
    }

    public final void setSequenceNo(final String seqNo) {
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

    public final BookAuthor getAuthor() {
        if (fieldDocument == null) {
            return null;
        }
        if (fieldAuthor == null) {
            final String firstName = XmlUtils.getString(fieldTitleInfo, "author/first-name").trim();
            final String lastName = XmlUtils.getString(fieldTitleInfo, "author/last-name").trim();
            fieldAuthor = new BookAuthor(firstName, lastName);
        }
        return fieldAuthor;
    }

    public final void setAuthor(final BookAuthor author) {
        if (fieldDocument == null) {
            return;
        }
        if (author == null) {
            return;
        }

        fieldAuthor = author;
        Element aElement = (Element) XmlUtils.selectNode(fieldTitleInfo, "author");
        if (aElement == null) {
            aElement = createElement(fieldTitleInfo, "author");
        }
        Element fnElement = (Element) XmlUtils.selectNode(aElement, "first-name");
        if (fnElement == null) {
            fnElement = createElement(aElement, "first-name");
        }
        fnElement.setTextContent(author.getFirstName());

        Element lnElement = (Element) XmlUtils.selectNode(aElement, "last-name");
        if (lnElement == null) {
            lnElement = createElement(aElement, "last-name");
        }
        lnElement.setTextContent(author.getLastName());
    }

    private Element createElement(final Element parent, final String... tagNames) {
        Element e = parent;
        for (final String tagName : tagNames) {
            e = (Element) e.appendChild(fieldDocument.createElement(tagName));
        }
        return e;
    }

}
