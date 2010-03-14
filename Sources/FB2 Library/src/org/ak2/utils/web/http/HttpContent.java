package org.ak2.utils.web.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.WebContentType;

public class HttpContent implements IWebContent {

    private final URL m_url;

    private final URLConnection m_conn;

    private final WebContentType m_type;

    private InputStream m_in;

    private int m_length;

    public HttpContent(final URL url) throws IOException {
        this(url, null);
    }

    public HttpContent(final URL url, final Proxy proxy) throws IOException {
        m_url = url;
        m_conn = openConnection(url, proxy);

        HttpStatus status = new HttpStatus(m_conn);
        if (!status.isOk()) {
            throw new HttpError(status);
        }

        m_type = new WebContentType(m_conn);
        m_length = m_conn.getContentLength();
        m_in = m_conn.getInputStream();
    }

    /**
     * @see org.ak2.utils.web.IWebContent#getUrl()
     */
    public final URL getUrl() {
        return m_url;
    }

    /**
     * @see org.ak2.utils.web.IWebContent#getType()
     */
    public final WebContentType getType() {
        return m_type;
    }

    /**
     * @see org.ak2.utils.web.IWebContent#getLength()
     */
    public final int getLength() {
        return m_length;
    }

    /**
     * @see org.ak2.utils.web.IWebContent#getStream()
     */
    public InputStream getStream() {
        return m_in;
    }

    /**
     * @see org.ak2.utils.web.IWebContent#getReader()
     */
    public Reader getReader() {
        return m_type.getReader(m_in);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpContent[m_url=" + m_url + ", m_type=" + m_type + ", m_length=" + m_length + "]";
    }

    private static URLConnection openConnection(final URL url, final Proxy proxy) throws IOException {
        return proxy != null ? url.openConnection(proxy) : url.openConnection();
    }
}
