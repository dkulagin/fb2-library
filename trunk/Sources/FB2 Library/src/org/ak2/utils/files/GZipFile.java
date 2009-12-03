package org.ak2.utils.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Alexander Kasatkin
 *
 */
public class GZipFile extends StandardFile {

    /**
     * Constructor.
     *
     * @param file real file
     */
    public GZipFile(File file) {
        super(file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.files.StandardFile#open()
     */
    @Override
    public InputStream open() throws IOException {
        return new GZIPInputStream(super.open());
    }

}
