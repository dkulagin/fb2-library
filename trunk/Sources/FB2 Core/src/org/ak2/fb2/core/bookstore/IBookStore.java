package org.ak2.fb2.core.bookstore;

import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;

public interface IBookStore {

    /**
     * Checks if the bookstore is exist.
     *
     * @return <code>true</code> if the bookstore is available
     */
    public boolean exists();

    public void clean();

    /**
     * @return number of stored books
     */
    public int getBookCount();

    /**
     * Gets the book.
     *
     * @param index the index
     *
     * @return an interface adapting internal book descriptor
     */
    IBook getBook(int index);

    /**
     * Gets the book.
     *
     * @param digest the digest
     *
     * @return an interface adapting internal book descriptor
     */
    IBook getBook(String digest);

    /**
     * Adds new book into the store
     *
     * @param file FB2 file
     * @return an index of the added book or <code>-1</code>
     */
    public int addBook(IFictionBookSource source);
}
