package org.ak2.fb2.core.bookstore.core;

import java.io.File;

import org.ak2.fb2.core.bookstore.Factory;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;

public abstract class AbstractTest {

    protected static File s_storeFolder;

    @BeforeClass
    public static void setUpClass() {
        s_storeFolder = new File(System.getProperty("user.home"), ".fb_store");
        Assert.assertNotNull("Folder object is not created", s_storeFolder);
        deleteDirectory(s_storeFolder);
    }

    @After
    public void clear() {
        deleteDirectory(s_storeFolder);
        Assert.assertFalse("Folder is not deleted", s_storeFolder.exists());
    }

    protected static void deleteDirectory(final File folder) {
        final IBookStore store = Factory.getProvider().openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        store.clean();
    }
}
