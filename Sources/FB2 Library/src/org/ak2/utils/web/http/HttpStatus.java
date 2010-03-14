package org.ak2.utils.web.http;

import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.web.WebException;

public class HttpStatus {

    private static final Pattern PATTERN = Pattern.compile("HTTP/(\\d\\.\\d)\\s(\\d+)\\s(.*)", Pattern.CASE_INSENSITIVE);

    private final String m_version;

    private final int m_code;

    private final String m_status;

    public HttpStatus(final String status) throws WebException {
        final Matcher m = PATTERN.matcher(status);
        if (m.matches() && m.groupCount() == 3) {
            m_version = m.group(1);
            m_code = Integer.parseInt(m.group(2));
            m_status = m.group(3);
        } else {
            throw new WebException("Bad HTTP status line: " + status);
        }
    }

    public HttpStatus(final URLConnection conn) throws WebException {
        this(getConnectionStatus(conn));
    }

    /**
     * @return the version
     */
    public final String getVersion() {
        return m_version;
    }

    /**
     * @return the code
     */
    public final int getCode() {
        return m_code;
    }

    /**
     * @return the status
     */
    public final String getStatus() {
        return m_status;
    }

    public final boolean isOk() {
        return 200 == getCode();
    }
    
    private static String getConnectionStatus(final URLConnection conn) {
        final List<String> list = conn.getHeaderFields().get(null);
        return LengthUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}
