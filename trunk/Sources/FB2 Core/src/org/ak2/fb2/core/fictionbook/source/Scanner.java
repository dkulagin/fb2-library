package org.ak2.fb2.core.fictionbook.source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Scanner {

    private static final Log LOGGER = LogFactory.getLog(Scanner.class);

    public static IFictionBookSource[] scanFolder(final File folder) {
        return scanFolder(folder, new ScanOptions());
    }

    public static IFictionBookSource[] scanFolder(final File folder, final ScanOptions scanOptions) {
        final ArrayList<IFictionBookSource> sources = new ArrayList<IFictionBookSource>();
        scanFolder(folder, scanOptions, sources);
        return sources.toArray(new IFictionBookSource[sources.size()]);
    }

    public static IFictionBookSource[] scanZipFile(final File file) {
        return scanZipFile(file, new ScanOptions());
    }

    public static IFictionBookSource[] scanZipFile(final File file, final ScanOptions scanOptions) {
        final ArrayList<IFictionBookSource> sources = new ArrayList<IFictionBookSource>();
        try {
            final ZipFile zipFile = new ZipFile(file);
            scanZipFile(zipFile, scanOptions, sources);
        } catch (final ZipException e) {
            LOGGER.error("", e);
            e.printStackTrace();
        } catch (final IOException e) {
            LOGGER.error("", e);
        }
        return sources.toArray(new IFictionBookSource[sources.size()]);
    }

    private static void scanFile(final File file, final ScanOptions options, final List<IFictionBookSource> sources) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        if (file.getName().endsWith(".fb2")) {
            sources.add(new FileSource(file));
        }
        if (file.getName().endsWith(".zip") && options.isScanZipFiles()) {
            try {
                final ZipFile zipFile = new ZipFile(file);
                scanZipFile(zipFile, options, sources);
            } catch (final ZipException e) {
                LOGGER.error("", e);
            } catch (final IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    private static void scanFolder(final File folder, final ScanOptions options, final List<IFictionBookSource> sources) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            return;
        }
        for (final File file : folder.listFiles()) {
            if (file.isDirectory() && options.isScanFolders()) {
                scanFolder(file, options, sources);
            } else {
                scanFile(file, options, sources);
            }
        }
    }

    private static void scanZipFile(final ZipFile zipFile, final ScanOptions options, final List<IFictionBookSource> sources) {
        if (zipFile == null) {
            return;
        }
        for (final Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            final ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().endsWith(".fb2")) {
                if (options.isScanZipFolders() || entry.getName().indexOf('/') == -1) {
                    sources.add(new ZipEntrySource(zipFile, entry));
                }
            }
        }
    }
}
