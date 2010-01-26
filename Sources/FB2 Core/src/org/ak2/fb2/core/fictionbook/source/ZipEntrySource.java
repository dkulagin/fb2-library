package org.ak2.fb2.core.fictionbook.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.ak2.fb2.core.utils.LengthUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZipEntrySource extends FileSource {

    private static final Log LOGGER = LogFactory.getLog(ZipEntrySource.class);

    private ZipFile fieldZipFile;

    private ZipEntry fieldZipEntry;

    private final String fieldZipFileName;

    private final String fieldZipEntryName;

    /**
     * The Constructor.
     *
     * @param zipFileName the zip file name
     * @param innerPath the inner path
     */
    public ZipEntrySource(final String zipFileName, final String innerPath) {
        super(zipFileName);
        fieldZipFileName = zipFileName;
        fieldZipEntryName = innerPath;
        try {
            fieldZipFile = new ZipFile(getFile());
            fieldZipEntry = fieldZipFile.getEntry(innerPath);
        } catch (final ZipException e) {
            LOGGER.error("", e);
        } catch (final IOException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * The Constructor.
     *
     * @param file the file
     * @param innerPath the inner path
     */
    public ZipEntrySource(final File file, final String innerPath) {
        super(file);
        fieldZipFileName = file.getPath();
        fieldZipEntryName = innerPath;
        try {
            fieldZipFile = new ZipFile(file);
            fieldZipEntry = fieldZipFile.getEntry(innerPath);
        } catch (final ZipException e) {
            LOGGER.error("", e);
        } catch (final IOException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * The Constructor.
     *
     * @param zipFile the zip file
     * @param innerPath the inner path
     */
    public ZipEntrySource(final ZipFile zipFile, final String innerPath) {
        super(new File(zipFile.getName()));
        fieldZipFileName = zipFile.getName();
        fieldZipEntryName = innerPath;
        fieldZipFile = zipFile;
        fieldZipEntry = fieldZipFile.getEntry(innerPath);
    }

    /**
     * The Constructor.
     *
     * @param zipFile the zip file
     * @param zipEntry the zip entry
     */
    public ZipEntrySource(final ZipFile zipFile, final ZipEntry zipEntry) {
        super(new File(zipFile.getName()));
        fieldZipFileName = zipFile.getName();
        fieldZipEntryName = zipEntry.getName();
        fieldZipFile = zipFile;
        fieldZipEntry = zipEntry;
    }

    /**
     * @see org.ak2.fb2.core.fictionbook.source.FileSource#exists()
     */
    @Override
    public boolean exists() {
        return super.exists() && fieldZipFile != null && fieldZipEntry != null;
    }

    /**
     * @see org.ak2.fb2.core.fictionbook.source.FileSource#createInputStream()
     */
    @Override
    public InputStream createInputStream() {
        try {
            return fieldZipFile.getInputStream(fieldZipEntry);
        } catch (final IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * @see org.ak2.fb2.core.fictionbook.source.FileSource#getBookFileName()
     */
    @Override
    public String getBookFileName() {
        return getZipEntryName();
    }

    /**
     * Gets the zip entry.
     *
     * @return the zip entry
     */
    public ZipEntry getZipEntry() {
        return fieldZipEntry;
    }

    /**
     * Gets the zip file.
     *
     * @return the zip file
     */
    public ZipFile getZipFile() {
        return fieldZipFile;
    }

    /**
     * @return the zipEntryName
     */
    public final String getZipEntryName() {
        return fieldZipEntryName;
    }

    /**
     * @return the zipFileName
     */
    public final String getZipFileName() {
        return fieldZipFileName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if (fieldZipFile != null && fieldZipEntry != null) {
            buf.append(fieldZipFileName);
            buf.append('!');
            buf.append(fieldZipEntryName);
        }
        return buf.toString();
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
        final ZipEntrySource that = (ZipEntrySource) obj;
        if (LengthUtils.equals(this.getFile(), that.getFile())) {
            return LengthUtils.equals(this.getZipEntryName(), that.getZipEntryName());

        }
        return false;
    }
}
