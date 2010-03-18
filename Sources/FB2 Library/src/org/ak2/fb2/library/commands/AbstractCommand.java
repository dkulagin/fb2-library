/**
 *
 */
package org.ak2.fb2.library.commands;

import org.ak2.utils.StreamUtils;
import org.ak2.utils.jlog.JLog;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

/**
 * @author Alexander Kasatkin
 *
 */
public abstract class AbstractCommand implements ICommand {

    protected static final JLogMessage MSG_ARGS = new JLogMessage(JLogLevel.INFO, "The ''{0}'' command is selected:\n\t{1}");

    protected static final JLogMessage MSG_INFO_VALUE = new JLogMessage(JLogLevel.INFO, "{0}: {1}");

    protected static final JLogMessage MSG_DEBUG_VALUE = new JLogMessage(JLogLevel.DEBUG, "{0}: {1}");

    protected static final JLogMessage MSG_ERROR = new JLogMessage(JLogLevel.ERROR, "Error on processing {0}: ");

    private final String name;

    private final String description;

    protected AbstractCommand(final String name) {
        this.name = name;
        this.description = StreamUtils.getResourceAsText(this.getClass(), "readme.txt", "Description is not available");
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    public static void logBoldLine() {
        logBoldLine(MSG_INFO_VALUE.getLevel());
    }

    public static void logBoldLine(final JLogLevel level) {
        JLog.log(level.getLevel(), "================================");
    }

    public static void logLine() {
        logLine(MSG_DEBUG_VALUE.getLevel());
    }

    public static void logLine(final JLogLevel level) {
        JLog.log(level.getLevel(), "--------------------------------");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
