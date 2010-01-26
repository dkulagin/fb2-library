package org.ak2.fb2.export.palmdoc;

import java.io.File;

import org.ak2.fb2.core.bookstore.Factory;
import org.ak2.fb2.core.bookstore.IBook;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.bookstore.IBookStoreProvider;
import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.bookstore.IFileCachProvider;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.core.operations.impl.LogOperationMonitor;
import org.ak2.fb2.export.palmdoc.ExportPalmDoc;
import org.ak2.fb2.export.palmdoc.ExportParameters;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExportPalmDocTest {

    private static final String TEST_FILE = "../../Books/klyensi_clovo_prezidenta.fb2";

    private static final String RESULT_FILE = "../../Books/klyensi_clovo_prezidenta.2.prc";

    private static File s_storeFolder;

    private static File s_resultFile;

    private static FileSource s_testFile;

    private static IBook s_book;

    @BeforeClass
    public static void init() {
        s_storeFolder = new File(System.getProperty("user.home"), ".fb_store");
        Assert.assertNotNull("Folder object is not created", s_storeFolder);

        s_testFile = new FileSource(TEST_FILE);
        Assert.assertNotNull(s_testFile);
        Assert.assertTrue(s_testFile.exists());

        s_resultFile = new File(RESULT_FILE);
        Assert.assertNotNull(s_resultFile);
        s_resultFile.delete();
        Assert.assertFalse(s_resultFile.exists());

        final IBookStoreProvider provider = Factory.getProvider();
        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);

        if (store.getBookCount() == 0) {
            Assert.assertEquals(0, store.addBook(s_testFile));
        }

        s_book = store.getBook(0);
        Assert.assertNotNull(s_book);
    }

    @Test
    public void testExecute() {
        final IFileCachProvider fileCachProvider = Factory.getFileCach();
        Assert.assertNotNull(fileCachProvider);
        final IFileCach fileCach = fileCachProvider.openFileCach(s_storeFolder);
        Assert.assertNotNull(fileCachProvider);

        final ExportParameters params = new ExportParameters();

        params.setCash(fileCach);
        params.setDescriptor(s_book);
        params.setResultFileName(RESULT_FILE);

        Assert.assertTrue(params.isValid());

        final IOperationMonitor monitor = new LogOperationMonitor();
        Assert.assertNotNull(monitor);

        final ExportPalmDoc operation = new ExportPalmDoc(params);
        Assert.assertNotNull(operation);

        operation.execute(monitor);
    }
}
