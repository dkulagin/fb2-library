/**
 * 
 */
package org.ak2.fb2.library.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        this.description = loadDescription(this.getClass());
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

    protected void logBoldLine(JLogLevel level) {
        JLog.log(level.getLevel(), "================================");
    }

    protected void logLine(JLogLevel level) {
        JLog.log(level.getLevel(), "--------------------------------");
    }

    static String loadDescription(Class<? extends AbstractCommand> clazz) {
        InputStream in = clazz.getResourceAsStream("readme.txt");
        if (in == null) {
            return "Description is not available";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder buf = new StringBuilder();
        try {
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                buf.append(s).append("\n");
            }
        } catch (IOException ex) {
        }
        return buf.toString();
    }
}
