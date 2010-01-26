package org.ak2.fb2.core.bookstore.exceptions;

import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;

public class UnsynchronizedBookException extends Exception {

    private static final long serialVersionUID = 1443943417410678548L;

    private final transient IFictionBookSource fieldSource;

    public UnsynchronizedBookException(final IFictionBookSource source) {
        super("Fiction book description is not sinchronized");
        fieldSource = source;
    }

    /**
     * @return the source
     */
    public final IFictionBookSource getSource() {
        return fieldSource;
    }
}
