package org.ak2.utils.web;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.ak2.utils.LengthUtils;

public class WebContentType {

    private static final String CHARSET_PREFIX = "charset=";

    private final String m_type;

    private final Charset m_charset;

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
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WebContentType [m_type=" + m_type + ", m_charset=" + m_charset + "]";
    }

}
