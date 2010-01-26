package org.ak2.fb2.core.bookstore;

import java.io.File;

public interface IBookStoreProvider {

    /**
     * Opens a bookstore stored in the given folder
     *
     * @param folder folder containing a bookstore
     * @return an instance of the {@link IBookStore} object
     */
    public IBookStore openBookStore(File folder);
}
