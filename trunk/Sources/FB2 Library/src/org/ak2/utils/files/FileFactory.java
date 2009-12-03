package org.ak2.utils.files;

import java.io.File;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public class FileFactory {

    /**
     * Creates an appropriate file wrapper
     * 
     * @param name file name
     * @return an instance of the {@link IFile} object or <code>null</code>
     */
    public static IFile create(String name) {
        return create(new File(name));
    }

    /**
     * Creates an appropriate file wrapper
     * 
     * @param parent parent folder
     * @param name file name
     * @return an instance of the {@link IFile} object or <code>null</code>
     */
    public static IFile create(File parent, String name) {
        return create(new File(parent, name));
    }

    /**
     * Creates an appropriate file wrapper
     * 
     * @param file real file
     * @return an instance of the {@link IFile} object or <code>null</code>
     */
    public static IFile create(File file) {
        if (file == null) {
            return null;
        }

        if (file.isDirectory()) {
            return new StandardFolder(file);
        }

        if (file.isFile()) {
            if (isZipFile(file)) {
                try {
                    return new ZipArchiveFile(file);
                } catch (final Exception ex) {
                    // NOP
                }
            } else if (isGZipFile(file)) {
                return new GZipFile(file);
            }

            return new StandardFile(file);
        }

        return null;
    }

    /**
     * Checks if the given file is a zip archive
     * 
     * @param file file to check
     * @return <code>true</code> if the given file is a zip archive
     */
    protected static boolean isZipFile(final File file) {
        if (file.exists()) {
            return isZipFile(file.getName());
        }
        return false;
    }

    /**
     * Checks if the given file is a zip archive
     * 
     * @param name file name to check
     * @return <code>true</code> if the given file is a zip archive
     */
    protected static boolean isZipFile(String name) {
        return LengthUtils.safeString(name).toLowerCase().endsWith(".zip");
    }

    /**
     * Checks if the given file is a gzip file
     * 
     * @param file file to check
     * @return <code>true</code> if the given file is a gzip file
     */
    protected static boolean isGZipFile(final File file) {
        if (file.exists()) {
            return isGZipFile(file.getName());
        }
        return false;
    }

    /**
     * Checks if the given file is a gzip file
     * 
     * @param fileName fileName to check
     * @return <code>true</code> if the given file is a gzip file
     */
    protected static boolean isGZipFile(String fileName) {
        final String name = fileName.toLowerCase();
        return name.endsWith(".gz");
    }

}
