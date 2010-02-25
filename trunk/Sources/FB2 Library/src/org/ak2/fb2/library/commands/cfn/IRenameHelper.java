package org.ak2.fb2.library.commands.cfn;

import java.util.Map;

import org.ak2.fb2.library.book.FictionBook;

public interface IRenameHelper {

    String AUTHOR = "author";
    String BOOK_NAME = "bookName";
    String BOOK_SEQUENCE = "bookSequence";
    String BOOK_SEQUENCE_NO = "bookSequenceNo";

    public Map<String, Object> getBookProperties(final FictionBook book);

    public void setBookProperties(final FictionBook book, final Map<String, Object> properties);
}
