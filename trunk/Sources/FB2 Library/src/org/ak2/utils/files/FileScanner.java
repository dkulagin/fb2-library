package org.ak2.utils.files;

import java.io.File;

/**
 * @author Alexander Kasatkin
 * 
 */
public class FileScanner {

    /**
     * Scanning the given file for an appropriate files.
     * 
     * @param root root folder or zip archive
     * @param filter file filter
     * @param options scanner options
     */
    public static void enumerate(final File root, final IFileFilter filter, final Options options) {
        final IFile f = FileFactory.create(root);
        if (f != null) {
            enumerate(f, filter, options);
        }
    }

    /**
     * Scanning the given file for an appropriate files.
     * 
     * @param root root folder or zip archive
     * @param filter file filter
     * @param options scanner options
     */
    public static void enumerate(final IFile root, final IFileFilter filter, final Options options) {
        if (root == null) {
            return;
        }

        if (root instanceof IFolder) {
            ((IFolder) root).enumerate(filter, wrapsOptions(options));
        }

        filter.accept(root);
    }

    /**
     * Wraps the <code>null</code> options with the default one.
     * 
     * @param options original options.
     * @return an instance of the {@link Options} object
     */
    public static Options wrapsOptions(final Options options) {
        return options != null ? options : new Options();
    }

    /**
     * This class defines file scanner options.
     */
    public static class Options {

        /**
         * Flag for recursive folder scanning
         */
        private boolean m_recursiveScanEnabled;

        /**
         * Flag for archive scanning.
         */
        private boolean m_archiveScanEnabled;

        /**
         * Constructor.
         */
        public Options() {
        }

        /**
         * Constructor.
         * 
         * @param recursiveScanEnabled the recursiveScanEnabled to set
         * @param archiveScanEnabled the archiveScanEnabled to set
         */
        public Options(boolean recursiveScanEnabled, boolean archiveScanEnabled) {
            m_recursiveScanEnabled = recursiveScanEnabled;
            m_archiveScanEnabled = archiveScanEnabled;
        }

        /**
         * @return the recursiveScanEnabled
         */
        public boolean isRecursiveScanEnabled() {
            return m_recursiveScanEnabled;
        }

        /**
         * @param recursiveScanEnabled the recursiveScanEnabled to set
         */
        public void setRecursiveScanAllowed(final boolean recursiveScanEnabled) {
            m_recursiveScanEnabled = recursiveScanEnabled;
        }

        /**
         * @return the archiveScanEnabled
         */
        public boolean isArchiveScanEnabled() {
            return m_archiveScanEnabled;
        }

        /**
         * @param archiveScanEnabled the archiveScanEnabled to set
         */
        public void setArchiveScanEnabled(final boolean archiveScanEnabled) {
            m_archiveScanEnabled = archiveScanEnabled;
        }

    }

}
