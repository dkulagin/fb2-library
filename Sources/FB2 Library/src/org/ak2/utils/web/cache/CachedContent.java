package org.ak2.utils.web.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.WebContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.ext.JSON;
import org.json.ext.JSONProperty;

/**
 * @author Alexander Kasatkin
 * 
 */
@JSON
public class CachedContent implements IWebContent {

    private final URL m_url;

    private final String m_id;

    private final String m_info;

    private final WebContentType m_type;

    private transient WeakReference<byte[]> m_content;

    public CachedContent(IWebContent content) throws IOException {
        m_url = content.getUrl();
        m_type = content.getType();
        m_info = content.getInfo();
        byte[] bytes = StreamUtils.getBytes(content.getStream());
        m_id = CacheManager.getInstance().saveToFile(bytes, m_type);
        m_content = new WeakReference<byte[]>(bytes);
    }

    public CachedContent(JSONObject object) throws JSONException, MalformedURLException {
        m_url = new URL(object.getString("url"));
        m_type = new WebContentType(object.getJSONObject("type"));
        m_id = object.getString("id");
        m_info = object.getString("info");
    }

    @Override
    @JSONProperty
    public URL getUrl() {
        return m_url;
    }

    @Override
    @JSONProperty
    public WebContentType getType() {
        return m_type;
    }

    @Override
    @JSONProperty
    public String getInfo() {
        return m_info;
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

    @JSONProperty
    public String getId() {
        return m_id;
    }

    byte[] getContent() throws IOException {
        byte[] content = m_content != null ? m_content.get() : null;
        if (content == null) {
            content = CacheManager.getInstance().loadFromFile(m_id);
            m_content = new WeakReference<byte[]>(content);
        }
        return content;
    }

    @Override
    public String toString() {
        return "CachedContent[" + m_id + (m_info != null ? ", " + m_info : "") + ", " + m_type + ", " + m_url + "]";
    }

}
