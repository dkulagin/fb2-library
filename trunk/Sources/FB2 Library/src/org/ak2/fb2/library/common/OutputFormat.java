package org.ak2.fb2.library.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.ak2.utils.zip.PackageCreator;

public enum OutputFormat {
    /**
     * FB2 output file.
     */
    Fb2,
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
        protected void writeFile(final File outFile, final String bookFileName, final byte[] content) throws IOException {
            final PackageCreator pc = new PackageCreator(outFile);
            pc.addFileToPackage(content, bookFileName);
            pc.close();
        }
    };

    private static final JLogMessage MSG_FILE_FOUND = new JLogMessage(JLogLevel.DEBUG, "File   found: {0}");

    private static final JLogMessage MSG_FILE_CREATED = new JLogMessage(JLogLevel.DEBUG, "File created: {0}");

    public File createFile(final File bookFolder, final String bookFileName, final FictionBook book) throws ProcessingException {
        try {
            return createFile(bookFolder, bookFileName, book.getBytes());
        } catch (ProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ProcessingException(ex);
        }
    }

    protected File createFile(final File bookFolder, final String bookFileName, final byte[] content) throws ProcessingException {
        final File outFile = getFile(bookFolder, bookFileName);
        if (!outFile.exists()) {
            try {
                writeFile(outFile, bookFileName, content);
                MSG_FILE_CREATED.log(outFile.getAbsolutePath());
                return outFile;
            } catch (IOException ex) {
                throw new ProcessingException(ex);
            }
        } else {
            MSG_FILE_FOUND.log(outFile.getAbsolutePath());
            throw new ProcessingException(outFile);
        }
    }

    protected File getFile(final File bookFolder, final String bookFileName) {
        return new File(bookFolder, bookFileName);
    }

    protected void writeFile(final File outFile, final String bookFileName, final byte[] content) throws IOException {
        final FileOutputStream out = new FileOutputStream(outFile);
        out.write(content);
        out.close();
    }
}
