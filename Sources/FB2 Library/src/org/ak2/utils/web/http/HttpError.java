package org.ak2.utils.web.http;

import org.ak2.utils.web.WebException;

public class HttpError extends WebException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7690993971912895079L;

    private final HttpStatus m_status;

    public HttpError(final HttpStatus status) {
        super("HTTP error: " + status.getCode() + " " + status.getStatus());
        m_status = status;
    }

    /**
     * @return the status
     */
    public final HttpStatus getStatus() {
        return m_status;
    }
}
