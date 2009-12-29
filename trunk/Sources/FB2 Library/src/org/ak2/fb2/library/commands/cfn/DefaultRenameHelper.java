package org.ak2.fb2.library.commands.cfn;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.utils.LengthUtils;

public class DefaultRenameHelper implements IRenameHelper {

    @Override
    public Map<String, String> getBookProperties(final FictionBook book) {
        final Map<String, String> map = new HashMap<String, String>();

        final String firstName = book.getAuthorFirstName();
        final String lastName = book.getAuthorLastName();
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

        final String author = (lastName + " " + firstName).trim();

        map.put(AUTHOR, author);
        map.put(AUTHOR_FIRST_NAME, firstName);
        map.put(AUTHOR_LAST_NAME, lastName);
        map.put(BOOK_NAME, bookName);
        map.put(BOOK_SEQUENCE, seq);
        map.put(BOOK_SEQUENCE_NO, seqNo);

        return map;
    }

    @Override
    public void setBookProperties(final FictionBook book, final Map<String, String> properties) {
        final String firstName = properties.get(AUTHOR_FIRST_NAME);
        if (firstName != null) {
            book.setAuthorFirstName(firstName);
        }
        final String lastName = properties.get(AUTHOR_LAST_NAME);
        if (lastName != null) {
            book.setAuthorLastName(lastName);
        }
        final String bookName = properties.get(BOOK_NAME);
        if (bookName != null) {
            book.setBookName(bookName);
        }
        final String seq = properties.get(BOOK_SEQUENCE);
        if (seq != null) {
            book.setSequence(seq);
        }
        final String seqNo = properties.get(BOOK_SEQUENCE_NO);
        if (seqNo != null) {
            book.setSequenceNo(seqNo);
        }
    }
}
