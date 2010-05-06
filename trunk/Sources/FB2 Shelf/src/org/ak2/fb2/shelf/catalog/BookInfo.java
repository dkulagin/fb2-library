package org.ak2.fb2.shelf.catalog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.library.book.FictionBookInfo;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.XmlUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

public class BookInfo implements Comparable<BookInfo> {

    /**
     * Escaped symbols
     */
    private static final Pattern ESCAPED = Pattern.compile("\\&\\#(\\d+)\\;");

    private final FileInfo m_fileInfo;

    private final BookAuthor m_author;

    private final String m_bookName;

    private final String m_sequence;

    private final String m_seqNo;

    private final Integer m_intSeqNo;

    public BookInfo(final String location, final Node root) throws Exception {
        final FictionBookInfo fbi = new FictionBookInfo(root);

        m_fileInfo = new FileInfo(normalize(location), normalize(XmlUtils.getString(root, "@container")), normalize(XmlUtils.getString(root, "@file")));

        m_author = fbi.getAuthor();
        m_bookName = fbi.getBookName();
        m_sequence = fbi.getSequence();
        m_seqNo = fbi.getSequenceNo();
        m_intSeqNo = fbi.getIntSequenceNo();
    }

    public BookInfo(final String location, final JSONObject book) throws Exception {
        m_fileInfo = new FileInfo(normalize(location), normalize(book.getString("container")), normalize(book.getString("file")));

        final JSONObject titleInfo = book.getJSONObject("title-info");
        m_bookName = normalize(titleInfo.getString("book-title"));

        final Object authorObject = titleInfo.get("author");
        JSONObject author = null;
        if (authorObject instanceof JSONObject) {
            author = titleInfo.getJSONObject("author");
        } else if (authorObject instanceof JSONArray) {
            final JSONArray authors = (JSONArray) authorObject;
            if (authors.length() > 0) {
                author = authors.getJSONObject(0);
            }
        }
        if (author != null) {
            m_author = new BookAuthor(normalize(author.optString("first-name")), normalize(author.optString("last-name")));
        } else {
            m_author = null;
        }

        final JSONObject seq = titleInfo.optJSONObject("sequence");
        if (seq != null) {
            m_sequence = LengthUtils.unsafeString(normalize(seq.optString("name")));
            if (m_sequence != null) {
                m_seqNo = normalize(seq.optString("number"));
                Integer number = null;
                if (LengthUtils.isNotEmpty(m_seqNo)) {
                    try {
                        number = Integer.parseInt(m_seqNo);
                    } catch (final Exception ex) {
                    }
                }
                m_intSeqNo = number;
            } else {
                m_seqNo = null;
                m_intSeqNo = null;
            }
        } else {
            m_sequence = null;
            m_seqNo = null;
            m_intSeqNo = null;
        }
    }

    public final FileInfo getFileInfo() {
        return m_fileInfo;
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

    @Override
    public int compareTo(final BookInfo that) {
        if (this == that) {
            return 0;
        }

        int result = this.getAuthor().compareTo(that.getAuthor());
        if (result == 0) {
            final String s1 = LengthUtils.safeString(this.getSequence());
            final String s2 = LengthUtils.safeString(that.getSequence());
            result = s1.compareTo(s2);

            if (result == 0) {
                final Integer no1 = this.getIntSequenceNo();
                final Integer no2 = that.getIntSequenceNo();

                if (no1 == null && no2 == null) {
                    result = 0;
                } else if (no1 != null && no2 != null) {
                    result = no1.compareTo(no2);
                } else if (no1 != null) {
                    result = 1;
                } else /* if (no2 != null) */{
                    result = -1;
                }
            }

            if (result == 0) {
                final String n1 = LengthUtils.safeString(this.getBookName());
                final String n2 = LengthUtils.safeString(that.getBookName());
                result = n1.compareTo(n2);
            }
        }

        return result < 0 ? -1 : result > 0 ? 1 : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BookInfo) {
            return 0 == compareTo((BookInfo) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_author == null) ? 0 : m_author.hashCode());
        result = prime * result + ((m_bookName == null) ? 0 : m_bookName.hashCode());
        result = prime * result + ((m_seqNo == null) ? 0 : m_seqNo.hashCode());
        result = prime * result + ((m_sequence == null) ? 0 : m_sequence.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "BookInfo [m_author=" + m_author + ", m_sequence=" + m_sequence + ", m_seqNo=" + m_seqNo + ", m_bookName=" + m_bookName + "]";
    }

    public static String normalize(String text) {
        final Matcher m = ESCAPED.matcher(text);
        final StringBuffer buf = new StringBuffer();
        while (m.find()) {
            final String code = m.group(1);
            final char ch = (char) Integer.parseInt(code);
            final String repl = "" + ch;
            m.appendReplacement(buf, repl);
        }
        m.appendTail(buf);
        return buf.toString();
    }
}
