package org.ak2.utils.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.ak2.utils.LengthUtils;

public class WebContentType implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -1955607275964692510L;

    private static final Map<String, String> MIME2EXT = new HashMap<String, String>();

    static {
        MIME2EXT.put("text", ".txt");
        MIME2EXT.put("text/plain", ".txt");
        MIME2EXT.put("text/html", ".html");
        MIME2EXT.put("text/xml", ".xml");
        MIME2EXT.put("image/gif", ".gif");
        MIME2EXT.put("image/jpeg", ".jpg");
        MIME2EXT.put("image/png", ".png");
    }

    private static final String CHARSET_PREFIX = "charset=";

    private String m_type;

    private Charset m_charset;

    public WebContentType(final URLConnection conn) {
        final String type = conn.getContentType();
        final String[] parts = type.split(";");
        m_type = parts[0].trim();

        String enc = conn.getContentEncoding();
        if (LengthUtils.isEmpty(enc) && parts.length > 0) {
            for (String s : parts) {
                s = s.trim().toLowerCase();
                if (s.startsWith(CHARSET_PREFIX)) {
                    enc = s.substring(CHARSET_PREFIX.length());
                    break;
                }
            }
        }
        m_charset = LengthUtils.isNotEmpty(enc) ? Charset.forName(enc) : null;
    }

    /**
     * @return the type
     */
    public final String getType() {
        return m_type;
    }

    /**
     * @return the charset
     */
    public final Charset getCharset() {
        return m_charset;
    }

    public Reader getReader(InputStream in) {
        return m_charset != null ? new InputStreamReader(in, m_charset) : new InputStreamReader(in);
    }

    public String getExtension() {
        String s = m_type;
        do {
            String ext = MIME2EXT.get(s);
            if (ext != null) {
                return ext;
            }
            int index = s.lastIndexOf("/");
            if (index >= 0) {
                s = s.substring(0, index);
            } else {
                s = null;
            }
        } while (LengthUtils.isNotEmpty(s));
        return "";
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return m_type + (m_charset != null ? ", " + m_charset : "");
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(m_type);
        out.writeObject(m_charset != null ? m_charset.name() : null);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        m_type = (String) in.readObject();
        String name = (String) in.readObject();
        if (name != null) {
            m_charset = Charset.forName(name);
        }
    }

}
