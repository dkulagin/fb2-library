package org.ak2.utils.jlog;

import org.ak2.utils.nls.NlsMessage;

/**
 * @author Alexander Kasatkin
 */
public class JLogMessage extends NlsMessage {

    /**
     * Message log level.
     */
    private JLogLevel m_level;

    /**
     * Constructor.
     * 
     * @param message message
     */
    public JLogMessage(final String message) {
        this(JLogLevel.DEBUG, message);
    }

    /**
     * Constructor.
     * 
     * @param level predefined log level
     * @param message message
     */
    public JLogMessage(final JLogLevel level, final String message) {
        super(message);
        m_level = level != null ? level : JLogLevel.DEBUG;
    }

    /**
     * Writes fatal message into the log.
     * 
     * @param args message arguments
     * @return formatted string
     */
    @Deprecated
    public final String fatal(final Object... args) {
        return log(JLogLevel.FATAL, args);
    }

    /**
     * Writes fatal message into the log.
     * 
     * @param th thrown exception
     * @param args message arguments
     * @return formatted string
     */
    @Deprecated
    public final String fatal(final Throwable th, final Object... args) {
        return log(JLogLevel.FATAL, th, args);
    }

    /**
     * Writes error message into the log.
     * 
     * @param args message arguments
     * @return formatted string
     */
    @Deprecated
    public final String error(final Object... args) {
        return log(JLogLevel.ERROR, args);
    }

    /**
     * Writes error message into the log.
     * 
     * @param th thrown exception
     * @param args message arguments
     * @return formatted string
     */
    @Deprecated
    public final String error(final Throwable th, final Object... args) {
        return log(JLogLevel.ERROR, th, args);
    }

    /**
     * Writes warning message into the log.
     * 
     * @param args message arguments
     * @return formatted string
     */
    @Deprecated
    public final String warning(final Object... args) {
        return log(JLogLevel.WARNING, args);
    }

    /**
     * Writes warning message into the log.
     * 
     * @param th thrown exception
     * @param args message arguments
     * @return formatted string
     */
    @Deprecated
    public final String warning(final Throwable th, final Object... args) {
        return log(JLogLevel.WARNING, th, args);
    }

    /**
     * Writes info message into the log.
     * 
     * @param args message arguments
     */
    @Deprecated
    public final void info(final Object... args) {
        log(JLogLevel.INFO, args);
    }

    /**
     * Writes debug message into the log.
     * 
     * @param args message arguments
     */
    @Deprecated
    public final void debug(final Object... args) {
        log(JLogLevel.DEBUG, args);
    }

    /**
     * Writes message into the log using message log level.
     * 
     * @param args message arguments
     * @return message text
     */
    public final String log(final Object... args) {
        return log(m_level, args);
    }

    /**
     * Writes message into the log using message log level.
     * 
     * @param th thrown exception
     * @param args message arguments
     * @return message text
     */
    public final String log(final Throwable th, final Object... args) {
        return log(m_level, th, args);
    }

    /**
     * Writes message into the log using the given log level.
     * 
     * @param level log level
     * @param args message arguments
     * @return message text
     */
    public final String log(final JLogLevel level, final Object... args) {
        String text = null;
        if (JLogLevel.isEnabled(level)) {
            text = this.bind(args);
            level.log(text);
        } else if (level != null && level.isError()) {
            text = this.bind(args);
        }
        return text;
    }

    /**
     * Writes message into the log using the given log level.
     * 
     * @param level log level
     * @param th thrown exception
     * @param args message arguments
     * @return message text
     */
    public final String log(final JLogLevel level, final Throwable th, final Object... args) {
        String text = null;
        if (JLogLevel.isEnabled(level)) {
            text = this.bind(args);
            level.log(text, th);
        } else if (level != null && level.isError()) {
            text = this.bind(args);
        }
        return text;
    }

    /**
     * @return <code>true</code> if log level of the message is enabled by DLOG.
     */
    public boolean isEnabled() {
        return JLogLevel.isEnabled(m_level);
    }

    /**
     * @return the level
     */
    public JLogLevel getLevel() {
        return m_level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(final JLogLevel level) {
        m_level = level;
    }

}
