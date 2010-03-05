/**
 * File: Activator.java
 * Abstract: TODO add abstract for test.editors.Activator.java
 *
 * @author: Whippet
 * @date: 07.08.2007 15:19:03
 *
 * History:
 *    [date] [comment]
 */

package test.editors;

import java.io.InputStream;
import java.util.logging.LogManager;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * TODO add comment for the class.
 *
 */
public class Activator extends AbstractUIPlugin {

    /**
     * Constructor.
     */
    public Activator() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/test/editors/java.log.properties");
        LogManager.getLogManager().readConfiguration(resourceAsStream);
    }
}
