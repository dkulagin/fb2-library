package org.ak2.utils.jlog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Alexander Kasatkin
 */
public class JLog {

    /**
     * Standard JDK logger
     */
    private static final Logger LOG;

    /**
     * Actual log level.
     */
    private static JLogLevel s_level = JLogLevel.DEBUG;

    private static ConsoleHandler s_consoleHandler;

    private static LogFormatter s_formatter;
    static {
        LOG = Logger.getLogger("org.ak2.utils.jlog");
        LOG.setUseParentHandlers(false);
        LOG.setLevel(s_level.getLevel());

        s_formatter = new LogFormatter();
        s_consoleHandler = new ConsoleHandler();
        s_consoleHandler.setLevel(s_level.getLevel());
        s_consoleHandler.setFormatter(s_formatter);

        LOG.addHandler(s_consoleHandler);
    }

    public static void setConsoleLevel(JLogLevel level) {
        s_consoleHandler.setLevel(level.getLevel());
    }

    public static void addLogFile(String pattern, JLogLevel level) {
        try {
            final FileHandler handler = new FileHandler(pattern);
            handler.setLevel(level.getLevel());
            handler.setFormatter(s_formatter);
            LOG.addHandler(handler);
        } catch (Exception ex) {
        }
    }

    public static void addLogHander(Handler handler) {
        if (handler != null) {
            LOG.addHandler(handler);
        }
    }

    /**
     * Get the log Level that has been specified for this Logger. The result may be null, which means that this logger's effective level will be inherited from
     * its parent.
     * 
     * @return this Logger's level
     */
    public static JLogLevel getLevel() {
        return s_level;
    }

    /**
     * Set the log level specifying which message levels will be logged by this logger. Message levels lower than this value will be discarded. The level value
     * Level.OFF can be used to turn off logging.
     * <p>
     * If the new level is null, it means that this node should inherit its level from its nearest ancestor with a specific (non-null) level value.
     * 
     * @param level the new value for the log level (may be null)
     */
    public static void setLevel(final JLogLevel level) {
        s_level = level != null ? level : JLogLevel.NONE;
        LOG.setLevel(s_level.getLevel());
    }

    /**
     * Check if a message of the given level would actually be logged by this logger. This check is based on the Loggers effective level, which may be inherited
     * from its parent.
     * 
     * @param level a message logging level
     * @return true if the given message level is currently being logged.
     * @see java.util.logging.Logger#isLoggable(java.util.logging.Level)
     */
    public static boolean isLoggable(final JLogLevel level) {
        return level != null ? LOG.isLoggable(level.getLevel()) : false;
    }

    /**
     * Log a SEVERE message.
     * <p>
     * If the logger is currently enabled for the SEVERE message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#severe(java.lang.String)
     */
    public static void severe(final String msg) {
        LOG.severe(msg);
    }

    /**
     * Log a WARNING message.
     * <p>
     * If the logger is currently enabled for the WARNING message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#warning(java.lang.String)
     */
    public static void warning(final String msg) {
        LOG.warning(msg);
    }

    /**
     * Log an INFO message.
     * <p>
     * If the logger is currently enabled for the INFO message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#info(java.lang.String)
     */
    public static void info(final String msg) {
        LOG.info(msg);
    }

    /**
     * Log a FINE message.
     * <p>
     * If the logger is currently enabled for the FINE message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#fine(java.lang.String)
     */
    public static void fine(final String msg) {
        LOG.fine(msg);
    }

    /**
     * Log a FINER message.
     * <p>
     * If the logger is currently enabled for the FINER message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#finer(java.lang.String)
     */
    public static void finer(final String msg) {
        LOG.finer(msg);
    }

    /**
     * Log a FINEST message.
     * <p>
     * If the logger is currently enabled for the FINEST message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#finest(java.lang.String)
     */
    public static void finest(final String msg) {
        LOG.finest(msg);
    }

    /**
     * Log a message, with no arguments.
     * <p>
     * If the logger is currently enabled for the given message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#log(java.util.logging.Level, java.lang.String)
     */
    public static void log(final Level level, final String msg) {
        LOG.log(level, msg);
    }

    /**
     * Log a message, with one object parameter.
     * <p>
     * If the logger is currently enabled for the given message level then a corresponding LogRecord is created and forwarded to all the registered output
     * Handler objects.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param param1 parameter to the message
     * @see java.util.logging.Logger#log(java.util.logging.Level, java.lang.String, java.lang.Object)
     */
    public static void log(final Level level, final String msg, final Object param1) {
        LOG.log(level, msg, param1);
    }

    /**
     * Log a message, with an array of object arguments.
     * <p>
     * If the logger is currently enabled for the given message level then a corresponding LogRecord is created and forwarded to all the registered output
     * Handler objects.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param params array of parameters to the message
     * @see java.util.logging.Logger#log(java.util.logging.Level, java.lang.String, java.lang.Object[])
     */
    public static void log(final Level level, final String msg, final Object[] params) {
        LOG.log(level, msg, params);
    }

    /**
     * Log a message, with associated Throwable information.
     * <p>
     * If the logger is currently enabled for the given message level then the given arguments are stored in a LogRecord which is forwarded to all registered
     * output handlers.
     * <p>
     * Note that the thrown argument is stored in the LogRecord thrown property, rather than the LogRecord parameters property. Thus is it processed specially
     * by output Formatters and is not treated as a formatting parameter to the LogRecord message property.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param thrown Throwable associated with log message.
     * @see java.util.logging.Logger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public static void log(final Level level, final String msg, final Throwable thrown) {
        LOG.log(level, msg, thrown);
    }

    /**
     * Log a message, specifying source class and method, with no arguments.
     * <p>
     * If the logger is currently enabled for the given message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#logp(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String)
     */
    public static void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg) {
        LOG.logp(level, sourceClass, sourceMethod, msg);
    }

    /**
     * Log a message, specifying source class and method, with a single object parameter to the log message.
     * <p>
     * If the logger is currently enabled for the given message level then a corresponding LogRecord is created and forwarded to all the registered output
     * Handler objects.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param msg The string message (or a key in the message catalog)
     * @param param1 Parameter to the log message.
     * @see java.util.logging.Logger#logp(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public static void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Object param1) {
        LOG.logp(level, sourceClass, sourceMethod, msg, param1);
    }

    /**
     * Log a message, specifying source class and method, with an array of object arguments.
     * <p>
     * If the logger is currently enabled for the given message level then a corresponding LogRecord is created and forwarded to all the registered output
     * Handler objects.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param msg The string message (or a key in the message catalog)
     * @param params Array of parameters to the message
     * @see java.util.logging.Logger#logp(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.Object[])
     */
    public static void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Object[] params) {
        LOG.logp(level, sourceClass, sourceMethod, msg, params);
    }

    /**
     * Log a message, specifying source class and method, with associated Throwable information.
     * <p>
     * If the logger is currently enabled for the given message level then the given arguments are stored in a LogRecord which is forwarded to all registered
     * output handlers.
     * <p>
     * Note that the thrown argument is stored in the LogRecord thrown property, rather than the LogRecord parameters property. Thus is it processed specially
     * by output Formatters and is not treated as a formatting parameter to the LogRecord message property.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param msg The string message (or a key in the message catalog)
     * @param thrown Throwable associated with log message.
     * @see java.util.logging.Logger#logp(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public static void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Throwable thrown) {
        LOG.logp(level, sourceClass, sourceMethod, msg, thrown);
    }

    /**
     * Log a message, specifying source class, method, and resource bundle name, with a single object parameter to the log message.
     * <p>
     * If the logger is currently enabled for the given message level then a corresponding LogRecord is created and forwarded to all the registered output
     * Handler objects.
     * <p>
     * The msg string is localized using the named resource bundle. If the resource bundle name is null, or an empty String or invalid then the msg string is
     * not localized.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param bundleName name of resource bundle to localize msg, can be null
     * @param msg The string message (or a key in the message catalog)
     * @param param1 Parameter to the log message.
     * @see java.util.logging.Logger#logrb(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public static void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg,
            final Object param1) {
        LOG.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
    }

    /**
     * Log a message, specifying source class, method, and resource bundle name, with an array of object arguments.
     * <p>
     * If the logger is currently enabled for the given message level then a corresponding LogRecord is created and forwarded to all the registered output
     * Handler objects.
     * <p>
     * The msg string is localized using the named resource bundle. If the resource bundle name is null, or an empty String or invalid then the msg string is
     * not localized.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param bundleName name of resource bundle to localize msg, can be null.
     * @param msg The string message (or a key in the message catalog)
     * @param params Array of parameters to the message
     * @see java.util.logging.Logger#logrb(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Object[])
     */
    public static void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg,
            final Object[] params) {
        LOG.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
    }

    /**
     * Log a message, specifying source class, method, and resource bundle name, with associated Throwable information.
     * <p>
     * If the logger is currently enabled for the given message level then the given arguments are stored in a LogRecord which is forwarded to all registered
     * output handlers.
     * <p>
     * The msg string is localized using the named resource bundle. If the resource bundle name is null, or an empty String or invalid then the msg string is
     * not localized.
     * <p>
     * Note that the thrown argument is stored in the LogRecord thrown property, rather than the LogRecord parameters property. Thus is it processed specially
     * by output Formatters and is not treated as a formatting parameter to the LogRecord message property.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param bundleName name of resource bundle to localize msg, can be null
     * @param msg The string message (or a key in the message catalog)
     * @param thrown Throwable associated with log message.
     * @see java.util.logging.Logger#logrb(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public static void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg,
            final Throwable thrown) {
        LOG.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
    }

    /**
     * Log a message, specifying source class, method, and resource bundle name with no arguments.
     * <p>
     * If the logger is currently enabled for the given message level then the given message is forwarded to all the registered output Handler objects.
     * <p>
     * The msg string is localized using the named resource bundle. If the resource bundle name is null, or an empty String or invalid then the msg string is
     * not localized.
     * <p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that issued the logging request
     * @param bundleName name of resource bundle to localize msg, can be null
     * @param msg The string message (or a key in the message catalog)
     * @see java.util.logging.Logger#logrb(java.util.logging.Level, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public static void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg) {
        LOG.logrb(level, sourceClass, sourceMethod, bundleName, msg);
    }

    /**
     * Log a method entry, with one parameter.
     * <p>
     * This is a convenience method that can be used to log entry to a method. A LogRecord with message "ENTRY {0}", log level FINER, and the given
     * sourceMethod, sourceClass, and parameter is logged.
     * <p>
     * 
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that is being entered
     * @param param1 parameter to the method being entered
     * @see java.util.logging.Logger#entering(java.lang.String, java.lang.String, java.lang.Object)
     */
    public static void entering(final String sourceClass, final String sourceMethod, final Object param1) {
        LOG.entering(sourceClass, sourceMethod, param1);
    }

    /**
     * Log a method entry, with an array of parameters.
     * <p>
     * This is a convenience method that can be used to log entry to a method. A LogRecord with message "ENTRY" (followed by a format {N} indicator for each
     * entry in the parameter array), log level FINER, and the given sourceMethod, sourceClass, and parameters is logged.
     * <p>
     * 
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that is being entered
     * @param params array of parameters to the method being entered
     * @see java.util.logging.Logger#entering(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public static void entering(final String sourceClass, final String sourceMethod, final Object[] params) {
        LOG.entering(sourceClass, sourceMethod, params);
    }

    /**
     * Log a method entry.
     * <p>
     * This is a convenience method that can be used to log entry to a method. A LogRecord with message "ENTRY", log level FINER, and the given sourceMethod and
     * sourceClass is logged.
     * <p>
     * 
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of method that is being entered
     * @see java.util.logging.Logger#entering(java.lang.String, java.lang.String)
     */
    public static void entering(final String sourceClass, final String sourceMethod) {
        LOG.entering(sourceClass, sourceMethod);
    }

    /**
     * Log a method return, with result object.
     * <p>
     * This is a convenience method that can be used to log returning from a method. A LogRecord with message "RETURN {0}", log level FINER, and the gives
     * sourceMethod, sourceClass, and result object is logged.
     * <p>
     * 
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of the method
     * @param result Object that is being returned
     * @see java.util.logging.Logger#exiting(java.lang.String, java.lang.String, java.lang.Object)
     */
    public static void exiting(final String sourceClass, final String sourceMethod, final Object result) {
        LOG.exiting(sourceClass, sourceMethod, result);
    }

    /**
     * Log a method return.
     * <p>
     * This is a convenience method that can be used to log returning from a method. A LogRecord with message "RETURN", log level FINER, and the given
     * sourceMethod and sourceClass is logged.
     * <p>
     * 
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of the method
     * @see java.util.logging.Logger#exiting(java.lang.String, java.lang.String)
     */
    public static void exiting(final String sourceClass, final String sourceMethod) {
        LOG.exiting(sourceClass, sourceMethod);
    }

    /**
     * Log throwing an exception.
     * <p>
     * This is a convenience method to log that a method is terminating by throwing an exception. The logging is done using the FINER level.
     * <p>
     * If the logger is currently enabled for the given message level then the given arguments are stored in a LogRecord which is forwarded to all registered
     * output handlers. The LogRecord's message is set to "THROW".
     * <p>
     * Note that the thrown argument is stored in the LogRecord thrown property, rather than the LogRecord parameters property. Thus is it processed specially
     * by output Formatters and is not treated as a formatting parameter to the LogRecord message property.
     * <p>
     * 
     * @param sourceClass name of class that issued the logging request
     * @param sourceMethod name of the method.
     * @param thrown The Throwable that is being thrown.
     * @see java.util.logging.Logger#throwing(java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public static void throwing(final String sourceClass, final String sourceMethod, final Throwable thrown) {
        LOG.throwing(sourceClass, sourceMethod, thrown);
    }

    /**
     * This class implements log entry formatter.
     */
    public static class LogFormatter extends Formatter {

        /**
         * Date time format.
         */
        private static final String FORMAT = "{0,date,dd.MM.yyyy} {0,time,HH:mm:ss.SSS}";

        /**
         * Message format.
         */
        private MessageFormat formatter;

        /**
         * Date object.
         */
        private final Date dat = new Date();

        /**
         * Arguments holder.
         */
        private final Object[] args = new Object[1];

        /**
         * Line separator string. This is the value of the line.separator property at the moment that the SimpleFormatter was created.
         */
        private final String lineSeparator = System.getProperty("line.separator");

        private boolean addDate = true;

        private boolean addThread = true;

        private boolean addLevel = true;

        private boolean addError = true;

        /**
         * Format the given LogRecord.
         * 
         * @param record the log record to be formatted.
         * @return a formatted log record
         */
        @Override
        public synchronized String format(final LogRecord record) {
            final StringBuffer sb = new StringBuffer();

            if (addDate) {
                // Minimize memory allocations here.
                dat.setTime(record.getMillis());
                args[0] = dat;
                final StringBuffer dateTimeText = new StringBuffer();
                if (formatter == null) {
                    formatter = new MessageFormat(FORMAT);
                }
                formatter.format(args, dateTimeText, null);
                sb.append(dateTimeText);
                sb.append(" ");
            }

            if (addLevel) {
                sb.append(record.getLevel().getLocalizedName());
            }

            if (addThread) {
                sb.append(" {");
                sb.append(Thread.currentThread().getName());
                sb.append("} ");
            }
            sb.append(formatMessage(record));
            sb.append(lineSeparator);
            if (addError && record.getThrown() != null) {
                try {
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (final Exception ex) {
                    // NOP
                }
            }
            return sb.toString();
        }

        /**
         * @return the addDate
         */
        public final boolean isAddDate() {
            return addDate;
        }

        /**
         * @return the addThread
         */
        public final boolean isAddThread() {
            return addThread;
        }

        /**
         * @return the addLevel
         */
        public final boolean isAddLevel() {
            return addLevel;
        }

        /**
         * @return the addError
         */
        public final boolean isAddError() {
            return addError;
        }

        /**
         * @param addDate the addDate to set
         */
        public final void setAddDate(boolean addDate) {
            this.addDate = addDate;
        }

        /**
         * @param addThread the addThread to set
         */
        public final void setAddThread(boolean addThread) {
            this.addThread = addThread;
        }

        /**
         * @param addLevel the addLevel to set
         */
        public final void setAddLevel(boolean addLevel) {
            this.addLevel = addLevel;
        }

        /**
         * @param addError the addError to set
         */
        public final void setAddError(boolean addError) {
            this.addError = addError;
        }
    }
}
