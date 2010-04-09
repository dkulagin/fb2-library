/**
 *
 */
package org.ak2.gui.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

/**
 * @author Whippet
 * 
 */
public class ResourceManager {
    private static final String DEFAULT_ROOT = "/";

    private static final String EXTENSION = ".properties";

    private static final JLogMessage MSG_ERROR = new JLogMessage(JLogLevel.ERROR, "Resource loading failed with the following exception:");

    private static final JLogMessage MSG_NORESOURCE = new JLogMessage(JLogLevel.ERROR, "No resource found for path [{0}]");

    private static final JLogMessage MSG_NOBUNDLE = new JLogMessage(JLogLevel.ERROR, "No bundle found for path [{0}] and locale [{1}]");

    private static ResourceManager s_instance;

    private final String m_root;

    private final Map<String, ImageIcon> m_icons = new HashMap<String, ImageIcon>();

    /**
     * Constructor
     * 
     * @param root
     *            resources root
     */
    protected ResourceManager(final String root) {
        m_root = buildRoot(root);
    }

    /**
     * Returns resource root path.
     * 
     * @return string
     */
    public String getResourceRoot() {
        return m_root;
    }

    /**
     * Gets an icon by the given relative or absolute path.
     * 
     * @param path
     *            path
     * @return an instance of the {@link ImageIcon} object or <code>null</code>
     */
    public ImageIcon getIcon(final String path) {
        ImageIcon icon = m_icons.get(path);
        if (icon == null && !m_icons.containsKey(path)) {
            m_icons.put(path, null);
            if (LengthUtils.isNotEmpty(path)) {
                final BufferedImage image = getImage(path);
                if (image != null) {
                    icon = new ImageIcon(image);
                }
                m_icons.put(path, icon);
            }
        }
        return icon;
    }

    /**
     * Gets an image by the given relative or absolute path.
     * 
     * @param path
     *            relative or absolute path
     * @return an instance of the {@link BufferedImage} object or <code>null</code>
     */
    public BufferedImage getImage(final String path) {
        final InputStream resource = getResource(path);
        if (resource != null) {
            try {
                return ImageIO.read(resource);
            } catch (IOException ex) {
                MSG_ERROR.log(ex);
            }
        }
        return null;
    }

    /**
     * Returns resource stream.
     * 
     * @param path
     *            relative or absolute resource path
     * @return an instance of the {@link InputStream} object or <code>null</code> object
     */
    public InputStream getResource(final String path) {
        if (LengthUtils.isEmpty(path)) {
            return null;
        }

        final List<String> resourcePaths = getResourcePaths(path, null);
        for (String resourcePath : resourcePaths) {
            final InputStream resource = getClass().getResourceAsStream(resourcePath);
            if (resource != null) {
                return resource;
            }
        }

        MSG_NORESOURCE.log(path);
        return null;
    }

    /**
     * Returns properties for the given relative or absolute path.
     * 
     * @param path
     *            property path
     * 
     * @return an instance of the {@link Properties} object
     */
    public Properties getProperties(final String path) {
        return getProperties(path, new Properties());
    }

    /**
     * Returns properties for the given relative or absolute path.
     * 
     * @param path
     *            property path
     * @param properties
     *            properties to fill
     * @return an instance of the {@link Properties} object
     */
    public Properties getProperties(final String path, final Properties properties) {
        if (LengthUtils.isNotEmpty(path)) {
            boolean found = false;
            final List<String> resourcePaths = getResourcePaths(path, EXTENSION);
            for (String resourcePath : resourcePaths) {
                final InputStream resource = getClass().getResourceAsStream(resourcePath);
                if (resource != null) {
                    try {
                        properties.load(resource);
                        found = true;
                    } catch (IOException ex) {
                        MSG_ERROR.log(ex);
                    }
                }
            }
            if (!found) {
                MSG_NORESOURCE.log(path);
            }
        }
        return properties;
    }

    /**
     * Returns a list of possible absolute resource paths.
     * 
     * @param path
     *            relative or absolute resource path
     * @param extension
     *            possible resource extension
     * @return a list of strings
     */
    protected List<String> getResourcePaths(final String path, final String extension) {
        List<String> paths = new LinkedList<String>();
        final String absolutePath = getResourcePath(m_root, path, extension);
        paths.add(absolutePath);

        final String defaultPath = getResourcePath(DEFAULT_ROOT, path, extension);
        if (!defaultPath.equals(absolutePath)) {
            paths.add(defaultPath);
        }
        return paths;
    }

    /**
     * Returns a singleton instance.
     * 
     * @return an instance of the {@link ResourceManager} object
     */
    public static synchronized ResourceManager getInstance() {
        if (s_instance == null) {
            s_instance = new ResourceManager(DEFAULT_ROOT);
        }
        return s_instance;
    }

    /**
     * Builds correct root resource path.
     * 
     * @param root
     *            root path
     * @return string starting and ending with <code>/</code> character
     */
    protected static String buildRoot(final String root) {
        StringBuilder buf = new StringBuilder(LengthUtils.safeString(root));
        if (buf.charAt(0) != '/') {
            buf.insert(0, '/');
        }
        for (int index = buf.indexOf("."); index != -1; index = buf.indexOf(".", index + 1)) {
            buf.setCharAt(index, '/');
        }
        if (buf.charAt(buf.length() - 1) != '/') {
            buf.append('/');
        }
        return buf.toString();
    }

    /**
     * Returns absolute resource path.
     * 
     * @param root
     *            root path
     * @param path
     *            relative or absolute resource path
     * @param extension
     *            possible resource extension
     * @return absolute resource path
     */
    protected static String getResourcePath(final String root, final String path, final String extension) {
        StringBuilder buf = new StringBuilder();
        if (!path.startsWith("/")) {
            buf.append(root);
        }
        buf.append(path);
        if (LengthUtils.isNotEmpty(extension) && !path.endsWith(extension)) {
            buf.append(extension);
        }
        return buf.toString();
    }
}
