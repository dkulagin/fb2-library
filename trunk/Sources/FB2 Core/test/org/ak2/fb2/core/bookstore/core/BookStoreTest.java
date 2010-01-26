package org.ak2.fb2.core.bookstore.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.zip.ZipException;

import org.ak2.fb2.core.bookstore.Factory;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.bookstore.IBookStoreProvider;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.ak2.fb2.core.fictionbook.source.ZipEntrySource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookStoreTest extends AbstractTest {

    private static final String TEST_FILE = "../../Books/klyensi_vse_strahi_mira.fb2";

    private static final String TEST_ZIP_FILE = "../../Books/klyensi_voenniye_deyistviya.zip";

    private static final String TEST_ZIP_ENTRY = "klyensi_voenniye_deyistviya.fb2";

    private static FileSource s_testFile;

    private static FileSource s_testZipEntry;

    @BeforeClass
    public static void init() throws ZipException, IOException {
        s_testFile = new FileSource(TEST_FILE);
        Assert.assertNotNull(s_testFile);
        Assert.assertTrue(s_testFile.exists());

        s_testZipEntry = new ZipEntrySource(TEST_ZIP_FILE, TEST_ZIP_ENTRY);
        Assert.assertNotNull(s_testZipEntry);
        Assert.assertTrue(s_testZipEntry.exists());
    }

    @Test
    public void testExists() {
        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertTrue(store.exists());
    }

    @Test
    public void testAddBook() {
        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertTrue(store.exists());

        assertEquals(-1, store.addBook(null));
        assertEquals(0, store.addBook(s_testFile));
        assertEquals(1, store.addBook(s_testZipEntry));
    }

    @Test
    public void testGetBookAdapter() {
        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertTrue(store.exists());
    }

    @Test
    public void testSaveBook() {
        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertTrue(store.exists());

        assertEquals(-1, store.addBook(null));
        assertEquals(0, store.addBook(s_testFile));
        assertEquals(1, store.addBook(s_testZipEntry));
    }
}
