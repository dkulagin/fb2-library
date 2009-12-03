package org.ak2.utils.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

/**
 * @author Alexander Kasatkin
 *
 */
public class ZipArchiveEntry implements IFile {

    /**
     * Archive.
     */
    private final ZipArchiveFile m_archive;

    /**
     * Archive entry.
     */
    private final ZipEntry m_entry;

    /**
     * Short entry name.
     */
    private final String m_name;

    /**
     * GZip flag.
     */
    private final boolean m_useGzip;

    /**
     * Constructor.
     *
     * @param archive the archive file
     * @param entry the archive entry
     */
    public ZipArchiveEntry(final ZipArchiveFile archive, final ZipEntry entry) {
        m_archive = archive;
        m_entry = entry;
        String name = entry.getName();
        int index = name.lastIndexOf(File.separator);
        if (index > -1) {
            name = name.substring(index + 1);
        }
        m_name = name;
        m_useGzip = FileFactory.isGZipFile(name);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getName()
     */
    public final String getName() {
        return m_name;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getFullName()
     */
    public final String getFullName() {
        return m_archive.getFullName() + "$" + m_entry.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getParent()
     */
    @Override
    public IFolder getParent() {
        return m_archive;
    }

    /**
     * @see org.ak2.utils.files.IFile#getRealFile()
     */
    @Override
    public File getRealFile() {
        return m_archive.getRealFile();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getDateTime()
     */
    public final Date getDateTime() {
        long time = m_entry.getTime();
        if (time == -1) {
            time = m_archive.getRealFile().lastModified();
        }
        return new Date(time);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#exists()
     */
    public final boolean exists() {
        return m_archive.exists();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#open()
     */
    public final InputStream open() throws IOException {
        InputStream in = m_archive.getArchive().getInputStream(m_entry);
        return m_useGzip ? new GZIPInputStream(in) : in;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[file=" + getFullName() + ", date=" + getDateTime() + "]";
    }

}
