package org.ak2.utils.files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Alexander Kasatkin
 *
 */
public class StandardFolder extends StandardFile implements IFolder {

    /**
     * Constructor.
     *
     * @param folder real folder
     */
    public StandardFolder(final File folder) {
        super(folder);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.IFolder#enumerate(org.ak2.utils.files.IFileFilter,
     *      org.ak2.utils.files.FileScanner.Options)
     */
    public final void enumerate(final IFileFilter filter, FileScanner.Options options) {
        if (getRealFile().isDirectory()) {
            final Filter f = new Filter(filter, options);

            getRealFile().list(f);

            for (IFolder folder : f) {
                folder.enumerate(filter, options);
            }
        }
    }

    /**
     * This class implements file filter for the {@link File#list(FilenameFilter) method.
     */
    protected static class Filter implements FilenameFilter, Iterator<IFolder>, Iterable<IFolder> {

        /**
         * Real filter.
         */
        private final IFileFilter m_filter;

        /**
         * Scanner options.
         */
        private final FileScanner.Options m_options;

        /**
         * Queue for children folders and archives.
         */
        private final Queue<IFolder> m_recursive = new LinkedList<IFolder>();

        /**
         * Constructor.
         *
         * @param filter the real filter
         * @param options the scanner options
         */
        public Filter(final IFileFilter filter, final FileScanner.Options options) {
            super();
            m_filter = filter;
            m_options = options;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(final File parent, final String name) {

            IFile file = FileFactory.create(new File(parent, name));

            if (file instanceof ZipArchiveFile) {
                processZipArchive((ZipArchiveFile) file);
                return false;
            }
            if (file instanceof IFolder) {
                processFolder((IFolder) file);
                return false;
            }

            m_filter.accept(file);

            return false;
        }

        /**
         * Processes the given ZIP archive
         *
         * @param file zip archive
         */
        protected void processZipArchive(final ZipArchiveFile file) {
            if (m_options.isArchiveScanEnabled()) {
                m_recursive.add(file);
            }

        }

        /**
         * Processes the given folder.
         *
         * @param file folder
         */
        protected void processFolder(final IFolder file) {
            if (m_options.isRecursiveScanEnabled()) {
                m_recursive.add(file);
            }
        }

        /**
         * {@inheritDoc}
         *
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return !m_recursive.isEmpty();
        }

        /**
         * {@inheritDoc}
         *
         * @see java.util.Iterator#next()
         */
        public IFolder next() {
            return m_recursive.poll();
        }

        /**
         * {@inheritDoc}
         *
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            m_recursive.poll();
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<IFolder> iterator() {
            return this;
        }
    }

}
