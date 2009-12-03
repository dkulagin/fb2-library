package org.ak2.utils.files;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Alexander Kasatkin
 */
public class ZipArchiveInternalEntry implements IFile {

    /**
     * Parent zip entry.
     */
    private final ZipArchiveEntry m_parent;

    /**
     * Internal stream.
     */
    private final ZipInputStream m_stream;

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
    public ZipArchiveInternalEntry(final ZipArchiveEntry parent, ZipInputStream stream, final ZipEntry entry) {
        m_parent = parent;
        m_stream = stream;
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
        return m_parent.getFullName() + "$" + m_entry.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getParent()
     */
    @Override
    public IFolder getParent() {
        return m_parent.getParent();
    }

    /**
     * @see org.ak2.utils.files.IFile#getRealFile()
     */
    @Override
    public File getRealFile() {
        return m_parent.getRealFile();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getDateTime()
     */
    public final Date getDateTime() {
        long time = m_entry.getTime();
        if (time == -1) {
            return m_parent.getDateTime();
        }
        return new Date(time);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#exists()
     */
    public final boolean exists() {
        return m_parent.exists();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#open()
     */
    public final InputStream open() throws IOException {
        InputStream in = new EntryStream();
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

    private class EntryStream extends FilterInputStream {

        public EntryStream() {
            super(m_stream);
        }

        /**
         * @see java.io.FilterInputStream#close()
         */
        @Override
        public void close() throws IOException {
            m_stream.closeEntry();
        }
    }
}
