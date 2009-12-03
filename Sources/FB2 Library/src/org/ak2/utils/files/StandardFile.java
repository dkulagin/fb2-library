package org.ak2.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Alexander Kasatkin
 *
 */
public class StandardFile implements IFile {

    /**
     * Real file.
     */
    private final File m_file;

    /**
     * Constructor.
     *
     * @param file real file
     */
    public StandardFile(final File file) {
        m_file = file;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getName()
     */
    public final String getName() {
        return m_file.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getFullName()
     */
    public final String getFullName() {
        return m_file.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getParent()
     */
    @Override
    public IFolder getParent() {
        File parent = m_file.getAbsoluteFile().getParentFile();
        return parent != null ? new StandardFolder(parent) : null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#getDateTime()
     */
    public final Date getDateTime() {
        return new Date(m_file.lastModified());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#exists()
     */
    public final boolean exists() {
        return m_file.exists();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFile#open()
     */
    public InputStream open() throws IOException {
        return new FileInputStream(m_file);
    }

    /**
     * @return the real file
     */
    public final File getRealFile() {
        return m_file;
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
