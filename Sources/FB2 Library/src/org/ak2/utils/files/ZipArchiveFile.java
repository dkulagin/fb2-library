package org.ak2.utils.files;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Alexander Kasatkin
 *
 */
public class ZipArchiveFile extends StandardFile implements IFolder {

    /**
     * Archive.
     */
    private final ZipFile m_archive;

    /**
     * Constructor.
     *
     * @param file real file
     * @throws IOException thrown on error
     * @throws ZipException thrown on error
     */
    public ZipArchiveFile(final File file) throws ZipException, IOException {
        super(file);
        m_archive = new ZipFile(file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFolder#enumerate(org.ak2.utils.files.IFileFilter,
     *      org.ak2.utils.files.FileScanner.Options)
     */
    public final void enumerate(final IFileFilter filter, final FileScanner.Options options) {
        if (options.isArchiveScanEnabled()) {
            final Enumeration<? extends ZipEntry> entries = m_archive.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry e = entries.nextElement();
                final ZipArchiveEntry entry = new ZipArchiveEntry(this, e);
                if (FileFactory.isZipFile(e.getName())) {
                    try {
                        ZipInputStream in = new ZipInputStream(entry.open());
                        for (ZipEntry childEntry = in.getNextEntry(); childEntry != null; childEntry = in
                                .getNextEntry()) {
                            ZipArchiveInternalEntry child = new ZipArchiveInternalEntry(entry, in, childEntry);
                            filter.accept(child);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    filter.accept(entry);
                }

            }
        }
    }

    /**
     * @return the archive
     */
    final ZipFile getArchive() {
        return m_archive;
    }
}
