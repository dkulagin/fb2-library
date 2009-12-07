package org.ak2.fb2.library.exceptions;

/**
 * @author Alexander Kasatkin
 * 
 */
public class LibraryException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -7464128977115433449L;

    public LibraryException() {
        super();
    }

    public LibraryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LibraryException(final String message) {
        super(message);
    }

    public LibraryException(final Throwable cause) {
        super(cause);
    }
}
