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

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(Throwable cause) {
        super(cause);
    }
}
