package org.ak2.fb2.library.commands.cfn;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.library.book.FictionBook;
import org.ak2.utils.LengthUtils;

public class DefaultRenameHelper implements IRenameHelper {

    @Override
    public Map<String, Object> getBookProperties(final FictionBook book) {
        final Map<String, Object> map = new HashMap<String, Object>();

        final BookAuthor author = book.getAuthor();
        String bookName = book.getBookName();
        String seq = book.getSequence();
        String seqNo = book.getSequenceNo();

        // Lib.rus.ec has a lot of books with name format: "Book title (Sequence name - sequence No)"
        final Pattern p = Pattern.compile("(.*)\\((.*)\\s-\\s([1-9][0-9]*)\\)");
        final Matcher m = p.matcher(bookName);
        if (LengthUtils.isEmpty(seq) && LengthUtils.isEmpty(seqNo) && m.matches()) {
            // Here we assign correct values for proper file naming
            bookName = m.group(1);
            seq = m.group(2);
            seqNo = m.group(3);
        }

        map.put(AUTHOR, author);
        map.put(BOOK_NAME, bookName);
        map.put(BOOK_SEQUENCE, seq);
        map.put(BOOK_SEQUENCE_NO, seqNo);

        return map;
    }

    @Override
    public void setBookProperties(final FictionBook book, final Map<String, Object> properties) {
        final BookAuthor author = (BookAuthor) properties.get(AUTHOR);
        if (author != null) {
            book.setAuthor(author);
        }
        final String bookName = (String) properties.get(BOOK_NAME);
        if (bookName != null) {
            book.setBookName(bookName);
        }
        final String seq = (String) properties.get(BOOK_SEQUENCE);
        if (seq != null) {
            book.setSequence(seq);
        }
        final String seqNo = (String) properties.get(BOOK_SEQUENCE_NO);
        if (seqNo != null) {
            book.setSequenceNo(seqNo);
        }
    }
}
