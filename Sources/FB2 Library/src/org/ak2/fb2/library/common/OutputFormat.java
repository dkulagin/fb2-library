package org.ak2.fb2.library.common;

import java.io.File;
import java.io.FileOutputStream;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.utils.zip.PackageCreator;

public enum OutputFormat {
    /**
     * FB2 output file.
     */
    Fb2 {
        /**
         * {@inheritDoc}
         * 
         * @see org.ak2.fb2.library.common.OutputFormat#getFile(java.io.File, java.lang.String)
         */
        @Override
        protected File getFile(final File bookFolder, final String bookFileName) {
            return new File(bookFolder, bookFileName);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.ak2.fb2.library.common.OutputFormat#writeFile(java.io.File, java.lang.String, org.ak2.fb2.library.book.FictionBook)
         */
        @Override
        protected void writeFile(final File outFile, final String bookFileName, final byte[] content) throws Exception {
            final FileOutputStream out = new FileOutputStream(outFile);
            out.write(content);
            out.close();
        }
    },
    /**
     * Zip file with single FB2 book.
     */
    Zip {
        /**
         * {@inheritDoc}
         * 
         * @see org.ak2.fb2.library.common.OutputFormat#getFile(java.io.File, java.lang.String)
         */
        @Override
        protected File getFile(final File bookFolder, final String bookFileName) {
            final String archiveFileName = bookFileName + ".zip";
            return new File(bookFolder, archiveFileName);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.ak2.fb2.library.common.OutputFormat#writeFile(java.io.File, java.lang.String, org.ak2.fb2.library.book.FictionBook)
         */
        @Override
        protected void writeFile(final File outFile, final String bookFileName, final byte[] content) throws Exception {
            final PackageCreator pc = new PackageCreator(outFile);
            pc.addFileToPackage(content, bookFileName);
            pc.close();
        }
    };

    public File createFile(final File bookFolder, final String bookFileName, final FictionBook book) throws Exception {
        return createFile(bookFolder, bookFileName, book.getBytes());
    }

    public File createFile(final File bookFolder, final String bookFileName, final byte[] content) throws Exception {
        final File outFile = getFile(bookFolder, bookFileName);
        if (!outFile.exists()) {
            writeFile(outFile, bookFileName, content);
            System.out.println("File created: " + outFile.getAbsolutePath());
            return outFile;
        } else {
            System.out.println("File found  : " + outFile.getAbsolutePath());
            return null;
        }
    }

    protected abstract File getFile(File bookFolder, String bookFileName);

    protected abstract void writeFile(File outFile, String bookFileName, byte[] content) throws Exception;

}
