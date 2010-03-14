package org.ak2.utils.web;

import java.io.IOException;

public class WebException extends IOException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3004277698379477815L;

    public WebException() {
        super();
    }

    public WebException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WebException(final String message) {
        super(message);
    }

    public WebException(final Throwable cause) {
        super(cause);
    }
}
