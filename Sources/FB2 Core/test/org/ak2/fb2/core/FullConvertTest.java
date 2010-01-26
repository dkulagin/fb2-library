package org.ak2.fb2.core;

import java.io.File;
import java.io.IOException;

import org.ak2.fb2.core.bookstore.Factory;
import org.ak2.fb2.core.bookstore.IBook;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.bookstore.IBookStoreProvider;
import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.bookstore.IFileCachProvider;
import org.ak2.fb2.core.bookstore.core.AbstractTest;
import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;
import org.ak2.fb2.core.fictionbook.source.ScanOptions;
import org.ak2.fb2.core.fictionbook.source.Scanner;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.core.operations.impl.LogOperationMonitor;
import org.ak2.fb2.core.utils.FileUtils;
import org.ak2.fb2.export.palmdoc.ExportPalmDoc;
import org.ak2.fb2.export.palmdoc.ExportParameters;
import org.junit.Assert;
import org.junit.Test;

public class FullConvertTest extends AbstractTest {

    private static final File FOLDER = new File("../../Books");
    private static final File PRC_FOLDER = new File("../../Books/Prc");

    @Test
    public void testFullConvert() throws IOException {

        FileUtils.deleteDirectory(PRC_FOLDER);
        PRC_FOLDER.mkdirs();
        Assert.assertEquals(true,PRC_FOLDER.exists());

        final IFictionBookSource[] result = Scanner.scanFolder(FOLDER, new ScanOptions(true, true, true));
        Assert.assertNotNull(result);

        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertTrue(store.exists());

        final IFileCachProvider fileCachProvider = Factory.getFileCach();
        Assert.assertNotNull(fileCachProvider);
        final IFileCach fileCach = fileCachProvider.openFileCach(s_storeFolder);
        Assert.assertNotNull(fileCachProvider);

        final ExportParameters params = new ExportParameters();

        final IOperationMonitor monitor = new LogOperationMonitor();
        Assert.assertNotNull(monitor);

        for (final IFictionBookSource source : result) {
            final int index = store.addBook(source);
            final IBook book = store.getBook(index);

            final File file = new File(PRC_FOLDER, org.ak2.fb2.core.utils.FileUtils.getFileName(source.getBookFileName(), "prc"));
            params.setCash(fileCach);
            params.setDescriptor(book);
            params.setResultFileName(file.getAbsolutePath());

            final ExportPalmDoc operation = new ExportPalmDoc(params);
            operation.execute(monitor);
        }
    }

}
