package org.ak2.utils.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public interface IWebContent {

    /**
     * @return the url
     */
    URL getUrl();

    /**
     * @return the type
     */
    WebContentType getType();

    /**
     * @return the length
     */
    int getLength() throws IOException;

    /**
     * @return the input stream
     */
    InputStream getStream() throws IOException;

    /**
     * @return the text content reader
     */
    Reader getReader() throws IOException;
}