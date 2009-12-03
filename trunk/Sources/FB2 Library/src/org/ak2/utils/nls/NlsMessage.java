package org.ak2.utils.nls;

import java.text.MessageFormat;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public class NlsMessage {

    /**
     * Raw string message.
     */
    private String m_message;

    /**
     * Message formatter.
     */
    private MessageFormat m_format;

    /**
     * Constructor.
     * 
     * @param message raw message
     */
    public NlsMessage(final String message) {
        m_message = message;
    }

    /**
     * @return the message
     */
    public final synchronized String getMessage() {
        return m_message;
    }

    /**
     * @param message the message to set
     */
    public final synchronized void setMessage(final String message) {
        m_message = message;
        m_format = null;
    }

    /**
     * Bind the given arguments to the message.
     * 
     * @param args message arguments
     * @return formatted string
     */
    public final String bind(final Object... args) {
        if (LengthUtils.isNotEmpty(args)) {
            return getFormat().format(args);
        }
        return m_message;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return raw message
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return m_message;
    }

    /**
     * Create a message formatter.
     * 
     * @return an instance of the {@link MessageFormat} class
     */
    private synchronized MessageFormat getFormat() {
        if (m_format == null) {
            m_format = new MessageFormat(m_message);
        }
        return m_format;
    }
}
