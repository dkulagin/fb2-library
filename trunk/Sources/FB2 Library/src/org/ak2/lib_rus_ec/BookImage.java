package org.ak2.lib_rus_ec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.ak2.utils.LengthUtils;

public class BookImage {

    private final BookPage m_bookPage;

    private final String m_id;

    private final URL m_link;

    private String m_contentType;

    public BookImage(final BookPage bookPage, final String link) throws MalformedURLException {
        super();
        m_bookPage = bookPage;

        final URL authorUrl = bookPage.getAuthorPage().getAuthorUrl();
        m_link = new URL(authorUrl.getProtocol(), authorUrl.getHost(), link);

        final int index = link.lastIndexOf("/");
        this.m_id = index < 0 ? link : link.substring(index + 1);
    }

    public BookPage getBookPage() {
        return m_bookPage;
    }

    public String getId() {
        return m_id;
    }

    public URL getLink() {
        return m_link;
    }

    public String getContentType() {
        return m_contentType;
    }

    public byte[] getContent() throws IOException {
        final URLConnection conn = m_link.openConnection();
        final InputStream in = conn.getInputStream();
        m_contentType = conn.getHeaderField("Content-Type");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buf = new byte[8192];
        for (int length = in.read(buf); length != -1; length = in.read(buf)) {
            out.write(buf, 0, length);
        }

        return out.toByteArray();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BookImage) {
            final BookImage that = (BookImage) obj;
            return LengthUtils.equals(this.m_id, that.m_id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return m_id.hashCode();
    }

    @Override
    public String toString() {
        return "BookImage [m_bookPage=" + m_bookPage + ", m_id=" + m_id + ", m_link=" + m_link + ", m_contentType=" + m_contentType + "]";
    }
}
