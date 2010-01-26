package org.ak2.fb2.core.fictionbook.source;

import java.io.File;

import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;
import org.ak2.fb2.core.fictionbook.source.ScanOptions;
import org.ak2.fb2.core.fictionbook.source.Scanner;
import org.junit.Assert;
import org.junit.Test;


public class ScannerTest {

    private static final File FOLDER = new File("../../Books");

    @Test
    public void testScanFiles() {
        final IFictionBookSource[] result = Scanner.scanFolder(FOLDER);
        Assert.assertNotNull(result);
        Assert.assertEquals(19, result.length);
    }

    @Test
    public void testRecursiveScan() {
        final IFictionBookSource[] result = Scanner.scanFolder(FOLDER, new ScanOptions(true, false, false));
        Assert.assertNotNull(result);
        Assert.assertEquals(20, result.length);
    }

    @Test
    public void testZipScan() {
        final IFictionBookSource[] result = Scanner.scanFolder(FOLDER, new ScanOptions(true, true, false));
        Assert.assertNotNull(result);
        Assert.assertEquals(21, result.length);
    }

    @Test
    public void testFullScan() {
        final IFictionBookSource[] result = Scanner.scanFolder(FOLDER, new ScanOptions(true, true, true));
        Assert.assertNotNull(result);

        for (final IFictionBookSource source : result) {
            System.out.println(source);
        }

        Assert.assertEquals(39, result.length);
    }

}
