package org.ak2.utils.web.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URL;

import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.WebContentType;

public class CachedContent implements IWebContent, Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2575586501915965048L;

    private final URL m_link;

    private final String m_id;

    private final WebContentType m_type;

    private transient WeakReference<byte[]> m_content;

    public CachedContent(IWebContent content) throws IOException {
        m_link = content.getUrl();
        m_type = content.getType();
        byte[] bytes = StreamUtils.getBytes(content.getStream());

        m_id = CacheManager.getInstance().saveToFile(bytes, m_type);
        m_content = new WeakReference<byte[]>(bytes);
    }

    @Override
    public URL getUrl() {
        return m_link;
    }

    @Override
    public WebContentType getType() {
        return m_type;
    }

    @Override
    public int getLength() throws IOException {
        byte[] content = getContent();
        return content != null ? content.length : -1;
    }

    @Override
    public Reader getReader() throws IOException {
        return m_type.getReader(getStream());
    }

    @Override
    public InputStream getStream() throws IOException {
        return new ByteArrayInputStream(getContent());
    }

    private byte[] getContent() throws IOException {
        byte[] content = m_content.get();
        if (content == null) {
            content = CacheManager.getInstance().loadFromFile(m_id);
            m_content = new WeakReference<byte[]>(content);
        }
        return content;
    }
}
