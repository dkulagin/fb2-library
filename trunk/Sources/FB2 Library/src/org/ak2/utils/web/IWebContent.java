package org.ak2.utils.web;

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
    int getLength();

    /**
     * @return the input stream
     */
    InputStream getStream();

    /**
     * @return the text content reader
     */
    Reader getReader();
}