package org.ak2.utils.nls;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public class Nls {

    /**
     * Property file extension.
     */
    private static final String EXTENSION = ".properties";

    /**
     * Loading error.
     */
    private static final NlsMessage LOADING_ERROR = new NlsMessage("Error loading {0}: {1}");

    /**
     * Field init error.
     */
    private static final NlsMessage FIELD_INIT_ERROR = new NlsMessage("Error setting message value for: {0}: {1}");

    /**
     * Missing message error.
     */
    private static final NlsMessage MISSING_MESSAGE_ERROR = new NlsMessage("NLS missing message: {0} in: {1}");

    /**
     * Unused message error.
     */
    private static final NlsMessage UNUSED_MESSAGE_ERROR = new NlsMessage("NLS unused message: {0} in: {1}");

    /**
     * Expected field modifiers.
     */
    private static final int EXPECTED_MODS = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

    /**
     * This object is assigned to the value of a field map to indicate that a translated message has already been
     * assigned to that field.
     */
    private static final Object ASSIGNED = new Object();

    /**
     * All possible locale suffixes.
     */
    private static String[] staticSuffixes;

    /**
     * Initialize the given class with the values from the specified message bundle.
     * 
     * @param clazz the class where the constants will exist
     */
    public static void initializeMessages(final Class<?> clazz) {
        initializeMessages(clazz.getName(), clazz);
    }

    /**
     * Initialize the given class with the values from the specified message bundle.
     * 
     * @param bundleName fully qualified path of the class name
     * @param clazz the class where the constants will exist
     */
    public static void initializeMessages(final String bundleName, final Class<?> clazz) {
        if (System.getSecurityManager() == null) {
            load(bundleName, clazz);
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                load(bundleName, clazz);
                return null;
            }
        });
    }

    /**
     * Load the given resource bundle using the specified class loader.
     * 
     * @param bundleName fully qualified path of the class name
     * @param clazz the class where the constants will exist
     */
    static void load(final String bundleName, final Class<?> clazz) {
        final Field[] fieldArray = clazz.getDeclaredFields();

        final boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;

        final Map<Object, Object> fields = new HashMap<Object, Object>(fieldArray.length * 2);
        for (final Field field : fieldArray) {
            fields.put(getKey(field.getName()), field);
        }
        final MessagesProperties properties = new MessagesProperties(fields, bundleName, isAccessible);
        final String[] variants = buildVariants(bundleName);
        for (final String variant : variants) {
            final InputStream input = getResourceAsStream(clazz, variant);
            if (input != null) {
                try {
                    properties.load(input);
                } catch (final IOException e) {
                    System.err.println(LOADING_ERROR.bind(variant, e.getMessage()));
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (final IOException e) {
                            // NOP
                        }
                    }
                }
            }
        }
        computeMissingMessages(bundleName, fields, fieldArray, isAccessible);
    }

    /**
     * Set the given value to the field.
     * 
     * @param field the field to fill
     * @param value the value to set
     * @param isAccessible <code>true</code> if class is accessible
     */
    static void setField(final Field field, final Object value, final boolean isAccessible) {
        try {
            if (!isAccessible) {
                field.setAccessible(true);
            }

            final NlsMessage msg = (NlsMessage) field.get(null);
            if (msg != null) {
                (msg).setMessage(LengthUtils.toString(value));
            }
        } catch (final Exception ex) {
            System.err.println(FIELD_INIT_ERROR.bind(field.getName(), ex.getMessage()));
        }
    }

    /**
     * Set the given value to the field.
     * 
     * @param field the field to fill
     * @param isAccessible <code>true</code> if class is accessible
     * @return field value
     */
    static String getField(final Field field, final boolean isAccessible) {
        try {
            if (!isAccessible) {
                field.setAccessible(true);
            }
            final NlsMessage msg = (NlsMessage) field.get(null);
            if (msg != null) {
                return (msg).getMessage();
            }
        } catch (final Exception ex) {
            System.err.println(FIELD_INIT_ERROR.bind(field.getName(), ex.getMessage()));
        }
        return null;
    }

    /**
     * Initialize missed messages.
     * 
     * @param bundleName fully qualified path of the class name
     * @param fieldMap map of class fields
     * @param fieldArray array of class fields
     * @param isAccessible <code>true</code> if class is accessible
     */
    static void computeMissingMessages(final String bundleName, final Map<Object, Object> fieldMap,
            final Field[] fieldArray, final boolean isAccessible) {
        for (final Field field : fieldArray) {

            if (!isNlsMessage(field)) {
                continue;
            }

            // if the field has a a value assigned, there is nothing to do
            if (fieldMap.get(getKey(field.getName())) == ASSIGNED) {
                continue;
            }

            if (LengthUtils.isNotEmpty(getField(field, isAccessible))) {
                continue;
            }

            final String value = MISSING_MESSAGE_ERROR.bind(field.getName(), bundleName);
            System.err.println(value);
            setField(field, value, isAccessible);
        }
    }

    /**
     * Returns key for the given field/property name
     * 
     * @param name field or property name
     * @return lower-case string
     */
    static String getKey(final Object name) {
        return LengthUtils.toString(name).toLowerCase();
    }

    /**
     * Tests this field.
     * 
     * @param f field to test
     * @return <code>true</code> if this field is a <code>public static final</code> field of the {@link NlsMessage}
     *         class or its successors.
     */
    static boolean isNlsMessage(final Field f) {
        final int m = f.getModifiers();

        if (m == EXPECTED_MODS) {
            final Class<?> type = f.getType();
            if (NlsMessage.class.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Build an array of property files to search. The returned array contains the property fields in order from most
     * specific to most generic. So, in the FR_fr locale, it will return file_fr_FR.properties, then file_fr.properties,
     * and finally file.properties.
     * 
     * @param bundleName fully qualified path of the class name
     * @return an array of all possible property file names
     */
    static String[] buildVariants(final String bundleName) {
        final String[] suffixes = getSuffixes();
        final String root = bundleName.replace('.', '/');
        final String[] variants = new String[suffixes.length];
        for (int i = 0; i < variants.length; i++) {
            variants[i] = root + suffixes[i];
        }
        return variants;
    }

    /**
     * Build an array of property files to search. The returned array contains the property fields in order from most
     * specific to most generic. So, in the FR_fr locale, it will return file_fr_FR.properties, then file_fr.properties,
     * and finally file.properties.
     * 
     * @return an array of all possible Locale suffixes
     */
    static String[] getSuffixes() {
        if (staticSuffixes == null) {
            String nl = Locale.getDefault().toString();
            final List<String> result = new ArrayList<String>(4);
            int lastSeparator;
            while (true) {
                result.add('_' + nl + EXTENSION);
                lastSeparator = nl.lastIndexOf('_');
                if (lastSeparator == -1) {
                    break;
                }
                nl = nl.substring(0, lastSeparator);
            }
            result.add(EXTENSION);
            staticSuffixes = result.toArray(new String[result.size()]);
        }
        return staticSuffixes;
    }

    /**
     * Returns an appropriate stream source for the given class.
     * 
     *@param name resource name
     * @param clazz class to load resources
     * @return an instance of the {@link IResourceStreamSource} class
     */
    static InputStream getResourceAsStream(final Class<?> clazz, final String name) {
        final ClassLoader loader = clazz.getClassLoader();
        if (loader != null) {
            return loader.getResourceAsStream(name);
        }
        return ClassLoader.getSystemResourceAsStream(name);
    }

    /**
     * Class which sub-classes java.util.Properties and uses the #put method to set field values rather than storing the
     * values in the table.
     */
    private static class MessagesProperties extends Properties {

        /**
         * Serial Version UID.
         */
        private static final long serialVersionUID = 7916712906200291812L;

        /**
         * Bundle name.
         */
        private final String bundleName;

        /**
         * Map of localized fields.
         */
        private final Map<Object, Object> fields;

        /**
         * Accessible flag.
         */
        private final boolean isAccessible;

        /**
         * Constructor.
         * 
         * @param bundleName fully qualified path of the class name
         * @param fieldMap map of class fields
         * @param isAccessible <code>true</code> if class is accessible
         */
        public MessagesProperties(final Map<Object, Object> fieldMap, final String bundleName,
                final boolean isAccessible) {
            super();
            this.fields = fieldMap;
            this.bundleName = bundleName;
            this.isAccessible = isAccessible;
        }

        /**
         * Sets the given value for a field with the given name.
         * 
         * @param key the field name.
         * @param value the value.
         * @return <code>null</code>
         * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public synchronized Object put(final Object key, final Object value) {

            final Object fieldObject = fields.put(getKey(key), ASSIGNED);
            if (fieldObject == ASSIGNED) {
                return null;
            }

            if (fieldObject == null) {
                System.err.println(UNUSED_MESSAGE_ERROR.bind(key, bundleName));
                return null;
            }

            final Field field = (Field) fieldObject;
            if (isNlsMessage(field)) {
                setField(field, value, isAccessible);
            }

            return null;
        }
    }

}
