package org.ak2.fb2.core.fictionbook.source;

import java.io.InputStream;

public interface IFictionBookSource {

    /**
     * @return <code>true</code> if fiction book source is available for loading
     */
    public boolean exists();

    /**
     * Creates the input stream. The returned stream should be closed on the caller side.
     *
     * @return an instance of the {@link InputStream} object or <code>null</code>
     */
    public InputStream createInputStream();

    /**
     * Gets the book file name.
     *
     * @return the book file name
     */
    public String getBookFileName();

}
