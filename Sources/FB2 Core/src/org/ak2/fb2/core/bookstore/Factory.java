package org.ak2.fb2.core.bookstore;

import org.ak2.fb2.core.bookstore.impl.DefaultFileCachProvider;
import org.ak2.fb2.core.bookstore.impl.XmlBookStoreProvider;

public class Factory {

    private static XmlBookStoreProvider s_instance;

    private static DefaultFileCachProvider s_cach;

    public static IBookStoreProvider getProvider() {
        if (s_instance == null) {
            s_instance = new XmlBookStoreProvider();
        }
        return s_instance;
    }

    public static IFileCachProvider getFileCach() {
        if (s_cach == null) {
            s_cach = new DefaultFileCachProvider();
        }
        return s_cach;
    }

}
