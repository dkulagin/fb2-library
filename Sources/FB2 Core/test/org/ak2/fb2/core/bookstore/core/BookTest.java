package org.ak2.fb2.core.bookstore.core;

import org.ak2.fb2.core.bookstore.Factory;
import org.ak2.fb2.core.bookstore.IBook;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.bookstore.IBookStoreProvider;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.ak2.fb2.core.fictionbook.source.ZipEntrySource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookTest extends AbstractTest {

    private static final String TEST_FILE = "../../Books/klyensi_vse_strahi_mira.fb2";

    private static final String TEST_ZIP_FILE = "../../Books/klyensi_voenniye_deyistviya.zip";

    private static final String TEST_ZIP_ENTRY = "klyensi_voenniye_deyistviya.fb2";

    private static FileSource s_testFile;

    private static FileSource s_testZipEntry;

    private static IBook s_book1;

    private static IBook s_book2;

    @BeforeClass
    public static void init() {
        s_testFile = new FileSource(TEST_FILE);
        Assert.assertNotNull(s_testFile);
        Assert.assertTrue(s_testFile.exists());

        s_testZipEntry = new ZipEntrySource(TEST_ZIP_FILE, TEST_ZIP_ENTRY);
        Assert.assertNotNull(s_testZipEntry);
        Assert.assertTrue(s_testZipEntry.exists());

        final IBookStoreProvider provider = Factory.getProvider();
        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertEquals(0, store.getBookCount());
        Assert.assertEquals(0, store.addBook(s_testFile));
        Assert.assertEquals(1, store.addBook(s_testZipEntry));

        s_book1 = store.getBook(0);
        Assert.assertNotNull(s_book1);
        s_book2 = store.getBook(1);
        Assert.assertNotNull(s_book2);
    }

    @Test
    public void testGetSource() {
        Assert.assertEquals(s_testFile, s_book1.getSource());
        Assert.assertEquals(s_testZipEntry, s_book2.getSource());
    }

    @Test
    public void testGetGenres() {
        final String[] genres = s_book1.getGenres();
        Assert.assertEquals(1, genres.length);
        Assert.assertEquals("thriller_mystery", genres[0]);
    }

    @Test
    public void testGetAuthors() {
        final String[] authors = s_book1.getAuthors();
        Assert.assertEquals(1, authors.length);
        Assert.assertEquals("��� ������", authors[0]);
    }

    @Test
    public void testGetTitle() {
        Assert.assertEquals("��� ������ ����", s_book1.getTitle());
    }

    @Test
    public void testGetAnnotation() {
        Assert.assertEquals(null, s_book1.getAnnotation());
    }

    @Test
    public void testGetKeywords() {
        Assert.assertEquals(null, s_book1.getKeywords());
    }

    @Test
    public void testGetDate() {
        Assert.assertEquals(null, s_book1.getDate());
    }

    @Test
    public void testGetLanguage() {
        Assert.assertEquals("ru", s_book1.getLanguage());
    }

    @Test
    public void testGetOriginalLanguage() {
        Assert.assertEquals(null, s_book1.getOriginalLanguage());
    }

    @Test
    public void testGetTranslators() {
        final String[] authors = s_book1.getTranslators();
        Assert.assertEquals(0, authors.length);
    }

    @Test
    public void testIsBelongToSequence() {
        Assert.assertFalse(s_book1.isBelongToSequence());
    }

    @Test
    public void testGetSequenceName() {
        Assert.assertNull(s_book1.getSequenceName());
    }

    @Test
    public void testGetOrginalNumber() {
        Assert.assertNull(s_book1.getOrginalNumber());
    }

}
