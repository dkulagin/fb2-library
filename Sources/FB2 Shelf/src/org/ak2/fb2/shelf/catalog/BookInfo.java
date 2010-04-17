package org.ak2.fb2.shelf.catalog;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.library.book.FictionBookInfo;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.XmlUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

public class BookInfo {

    private final String m_location;

    private final String m_container;

    private final String m_file;

    private final BookAuthor m_author;

    private final String m_bookName;

    private final String m_sequence;

    private final String m_seqNo;

    private final Integer m_intSeqNo;

    public BookInfo(String location, Node root) throws Exception {
        FictionBookInfo fbi = new FictionBookInfo(root);
        m_location = location;
        m_container = XmlUtils.getString(root, "@container");
        m_file = XmlUtils.getString(root, "@file");
        m_author = fbi.getAuthor();
        m_bookName = fbi.getBookName();
        m_sequence = fbi.getSequence();
        m_seqNo = fbi.getSequenceNo();
        m_intSeqNo = fbi.getIntSequenceNo();
    }

    public BookInfo(String location, JSONObject book) throws Exception {
        m_location = location;
        m_container = book.getString("container");
        m_file = book.getString("file");

        JSONObject titleInfo = book.getJSONObject("title-info");

        m_bookName = titleInfo.getString("book-title");

        
        Object authorObject = titleInfo.get("author");
        JSONObject author = null;
        if (authorObject instanceof JSONObject) {
            author = titleInfo.getJSONObject("author");
        } else if (authorObject instanceof JSONArray) {
            JSONArray authors = (JSONArray) authorObject;
            if (authors.length() > 0) {
                author = authors.getJSONObject(0);
            }
        }
        if (author != null) { 
            m_author = new BookAuthor(author.optString("first-name"), author.optString("last-name"));
        } else {
            m_author = null;
        }

        JSONObject seq = titleInfo.optJSONObject("sequence");
        if (seq != null) {
            m_sequence = seq.getString("name");
            m_seqNo = seq.optString("number");
            Integer number = null;
            if (LengthUtils.isNotEmpty(m_seqNo)) {
                try {
                    number = Integer.parseInt(m_seqNo);
                } catch (Exception ex) {
                }
            } 
            m_intSeqNo = number; 
        } else {
            m_sequence = null;
            m_seqNo = null;
            m_intSeqNo = null;
        }
    }

    public String getLocation() {
        return m_location;
    }

    public String getContainer() {
        return m_container;
    }

    public String getFile() {
        return m_file;
    }

    /**
     * @return the author
     */
    public final BookAuthor getAuthor() {
        return m_author;
    }

    /**
     * @return the bookName
     */
    public final String getBookName() {
        return m_bookName;
    }

    /**
     * @return the sequence
     */
    public final String getSequence() {
        return m_sequence;
    }

    /**
     * @return the seqNo
     */
    public final String getSequenceNo() {
        return m_seqNo;
    }

    /**
     * @return the intSeqNo
     */
    public final Integer getIntSequenceNo() {
        return m_intSeqNo;
    }
}
