package org.ak2.fb2.core.fictionbook;

import org.ak2.fb2.core.fictionbook.FictionBook;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

public class FictionBookTest {

    private static final String TEST_FILE_1251 = "../../Books/klyensi_vse_strahi_mira.fb2";

    private static final String TEST_FILE_1252 = "../../Books/clancy_patriot_games.fb2";

    @Test
    public void testWin1251() {
        final FileSource fileSource = new FileSource(TEST_FILE_1251);
        Assert.assertNotNull(fileSource);
        Assert.assertTrue(fileSource.exists());

        final FictionBook book = new FictionBook(fileSource);
        Assert.assertNotNull(book);

        final Document document = book.getDocument();
        Assert.assertNotNull(document);

        final String inputEncoding = document.getXmlEncoding();
        Assert.assertEquals("windows-1251", inputEncoding);
    }

    @Test
    public void testWin1252() {
        final FileSource fileSource = new FileSource(TEST_FILE_1252);
        Assert.assertNotNull(fileSource);
        Assert.assertTrue(fileSource.exists());

        final FictionBook book = new FictionBook(fileSource);
        Assert.assertNotNull(book);

        final Document document = book.getDocument();
        Assert.assertNotNull(document);

        final String inputEncoding = document.getXmlEncoding();
        Assert.assertEquals("windows-1252", inputEncoding);
    }

}
