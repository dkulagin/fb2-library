package org.ak2.fb2.core.fictionbook.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ak2.fb2.core.utils.LengthUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileSource implements IFictionBookSource {

    private static final Log LOGGER = LogFactory.getLog(FileSource.class);

    private final File fieldFile;

    /**
     * The Constructor.
     *
     * @param fileName the file name
     */
    public FileSource(final String fileName) {
        this.fieldFile = new File(fileName);
    }

    /**
     * The Constructor.
     *
     * @param file the fiction book file
     */
    public FileSource(final File file) {
        this.fieldFile = file;
    }

    /**
     * @see org.ak2.fb2.core.fictionbook.source.IFictionBookSource#exists()
     */
    public boolean exists() {
        return fieldFile != null && fieldFile.exists();
    }

    /**
     * @see org.ak2.fb2.core.fictionbook.source.IFictionBookSource#createInputStream()
     */
    public InputStream createInputStream() {
        try {
            return new FileInputStream(fieldFile);
        } catch (final FileNotFoundException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * @see org.ak2.fb2.core.fictionbook.source.IFictionBookSource#getBookFileName()
     */
    public String getBookFileName() {
        return fieldFile.getName();
    }

    /**
     * Gets the file.
     *
     * @return the source file
     */
    public File getFile() {
        return fieldFile;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return fieldFile != null ? fieldFile.getPath() : "";
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileSource that = (FileSource) obj;
        return LengthUtils.equals(this.getFile(), that.getFile());
    }
}
