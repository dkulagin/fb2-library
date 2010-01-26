package org.ak2.fb2.core.bookstore.impl;

import java.io.File;

import org.ak2.fb2.core.bookstore.IFileCach;

class DefaultFileCach implements IFileCach {

    private static final String CACH_FOLDER_NAME = ".cach";

    private final File fieldCach;

    /**
     * The Constructor.
     *
     * @param folder the folder
     */
    public DefaultFileCach(final File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        fieldCach = new File(folder, CACH_FOLDER_NAME);
        fieldCach.mkdir();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IFileCach#getCachedFile(java.lang.String, java.lang.String)
     */
    public File getCachedFile(final String ownerDigest, final String fileName) {
        if (fieldCach.exists()) {
            final File ownerFolder = new File(fieldCach, ownerDigest);
            ownerFolder.mkdir();
            if (ownerFolder.exists()) {
                return new File(ownerFolder, fileName);
            }
            else {
                throw new IllegalStateException("Digest folder not exist: " + ownerFolder.getAbsolutePath());
            }
        }
        else {
            throw new IllegalStateException("Cach root folder not exist: " + fieldCach.getAbsolutePath());
        }
        //return null;
    }
}
