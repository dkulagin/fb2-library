package org.ak2.fb2.core.bookstore.impl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.bookstore.IBookStoreProvider;
import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.bookstore.IFileCachProvider;

public class XmlBookStoreProvider implements IBookStoreProvider, IFileCachProvider {

    private final HashMap<File, WeakReference<IBookStore>> fieldMap = new HashMap<File, WeakReference<IBookStore>>();

    /**
     * @see org.ak2.fb2.core.bookstore.IBookStoreProvider#openBookStore(java.io.File)
     */
    public IBookStore openBookStore(final File folder) {
        final WeakReference<IBookStore> ref = fieldMap.get(folder);
        IBookStore bookStore = ref != null ? ref.get() : null;
        if (bookStore == null) {
            bookStore = new XmlBookStore(folder);
            fieldMap.put(folder, new WeakReference<IBookStore>(bookStore));
        }
        return bookStore;
    }

    public IFileCach openFileCach(final File folder) {
        return new DefaultFileCach(folder);
    }

}
