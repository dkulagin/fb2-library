/**
 * 
 */
package org.ak2.fb2.library.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Alexander Kasatkin
 * 
 */
public abstract class AbstractCommand implements ICommand {

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
