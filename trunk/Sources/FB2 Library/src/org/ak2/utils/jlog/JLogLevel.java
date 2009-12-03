package org.ak2.utils.jlog;

import java.util.logging.Level;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public enum JLogLevel {

    /**
     * @see DLog#L_NONE
     */
    NONE(Level.OFF, true) {
        /**
         * @return <code>false</code>
         */
        public boolean isEnabled() {
            return false;
        }
    },
    /**
     * @see DLog#L_FATAL
     */
    FATAL(Level.SEVERE, true),
    /**
     * @see DLog#L_ERROR
     */
    ERROR(Level.SEVERE, true),
    /**
     * @see DLog#L_WARNING
     */
    WARNING(Level.WARNING, true),
    /**
     * @see DLog#L_INFO
     */
    INFO(Level.INFO, false),
    /**
     * @see DLog#L_DEBUG
     */
    DEBUG(Level.FINE, false);

    /**
     * Log level.
     */
    private final Level m_level;

    /**
     * Error flag.
     */
    private final boolean m_error;

    /**
     * Constructor.
     * 
     * @param level log level
     * @param error error flag
     */
    private JLogLevel(Level level, boolean error) {
        m_level = level;
        m_error = error;
    }

    /**
     * @return the level
     */
    public Level getLevel() {
        return m_level;
    }

    /**
     * Adds entry to log.
     * 
     * @param msg the log message
     */
    public final void log(String msg) {
        if (isEnabled()) {
            JLog.log(m_level, msg);
        }
    }

    /**
     * Adds entry to log.
     * 
     * @param msg the log message
     * @param t throwable object associated with this entry (usually an exception) or <code>null</code> if there is
     *        none.
     */
    public final void log(String msg, final Throwable t) {
        if (isEnabled()) {
            JLog.log(m_level, msg, t);
        }
    }

    /**
     * @return <code>true</code> if this log level is enabled by DLOG.
     */
    public boolean isEnabled() {
        return JLog.isLoggable(this);
    }

    /**
     * @return the error flag
     */
    public boolean isError() {
        return m_error;
    }

    /**
     * @param levels log levels to check
     * @return <code>true</code> if this log level is enabled by DLOG.
     */
    public static boolean isEnabled(final JLogLevel... levels) {
        if (LengthUtils.isEmpty(levels)) {
            return false;
        }
        for (JLogLevel level : levels) {
            if (level == null || !level.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param level log level to check
     * @return <code>true</code> if this log level means error.
     */
    public static boolean isError(final JLogLevel level) {
        return level != null && level.isError();
    }

}
