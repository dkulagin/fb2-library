package org.ak2.fb2.library.exceptions;

/**
 * @author Alexander Kasatkin
 * 
 */
public class BadCmdArguments extends LibraryException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -9148905003158696709L;

    private final boolean showReadme;

    public BadCmdArguments(final String message) {
        this(message, false);
    }

    public BadCmdArguments(final String message, final boolean showReadme) {
        super(message);
        this.showReadme = showReadme;
    }

    /**
     * @return the showReadme
     */
    public final boolean isShowReadme() {
        return showReadme;
    }
}
