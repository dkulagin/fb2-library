package org.ak2.utils.enums;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public class EnumUtils {

    /**
     * Returns enumeration item of the given class.
     * 
     * @param <T> type of enumeration item
     * @param clazz enumeration class
     * @param object one of the following possible values:
     *        <ul>
     *        <li>an instance of the enumeration class</li>
     *        <li>a {@link Number} object containing value of an enumeration item.</li>
     *        <li>a {@link String} object containing name of enumeration item (case insensitive)</li>
     *        <li>a {@link String} object containing integer value of an enumeration item</li>
     *        </ul>
     * @return an instance of the given enumeration class or <code>null</code> if an appropriate item cannot be found
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & IntConstant> T itemOf(final Class<T> clazz, final Object object) {
        if (object == null) {
            return null;
        }
        if (clazz.isInstance(object)) {
            return (T) object;
        }
        final String nameOrValue = object.toString();
        try {
            final int value = Integer.parseInt(nameOrValue);
            return valueOf(clazz, value);
        } catch (final NumberFormatException ex) {
            return valueOf(clazz, nameOrValue);
        }
    }

    /**
     * Returns enumeration item of the given class.
     * 
     * @param <T> type of enumeration item
     * @param clazz enumeration class
     * @param value value of an enumeration item.</li>
     * @return an instance of the given enumeration class or <code>null</code>if an appropriate item cannot be found
     */
    public static <T extends Enum<T> & IntConstant> T valueOf(final Class<T> clazz, final int value) {
        final T[] values = clazz.getEnumConstants();
        if (LengthUtils.isNotEmpty(values)) {
            for (final T item : values) {
                if (value == item.value()) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Returns enumeration item of the given class or an integer constant if integer value is not included into the
     * enumeration.
     * 
     * @param <T> type of enumeration item
     * @param clazz enumeration class
     * @param object one of the following possible values:
     *        <ul>
     *        <li>an instance of the enumeration class</li>
     *        <li>a {@link Number} object containing value of an enumeration item.</li>
     *        <li>a {@link String} object containing name of enumeration item (case insensitive)</li>
     *        <li>a {@link String} object containing integer value of an enumeration item</li>
     *        </ul>
     * @return an instance of {@link IntConstant} object or <code>null</code>if source object is
     *         <code>null<code> or contained unknown item name
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & IntConstant> IntConstant constOf(final Class<T> clazz, final Object object) {
        if (object == null) {
            return null;
        }
        if (clazz.isInstance(object)) {
            return (T) object;
        }
        final String nameOrValue = object.toString();
        try {
            final int value = Integer.parseInt(nameOrValue);
            return constOf(clazz, value);
        } catch (final NumberFormatException ex) {
            return valueOf(clazz, nameOrValue);
        }
    }

    /**
     * Returns enumeration item of the given class or an integer constant if integer value is not included into the
     * enumeration.
     * 
     * @param <T> type of enumeration item
     * @param clazz enumeration class
     * @param value value of an enumeration item.</li>
     * @return an instance of {@link IntConstant} object or <code>null</code> if source object cannot be parsed
     */
    public static <T extends Enum<T> & IntConstant> IntConstant constOf(final Class<T> clazz, final int value) {
        final T[] values = clazz.getEnumConstants();
        if (LengthUtils.isNotEmpty(values)) {
            for (final T item : values) {
                if (value == item.value()) {
                    return item;
                }
            }
        }
        return new Unknown(value);
    }

    /**
     * Returns enumeration item of the given class.
     * 
     * @param <T> type of enumeration item
     * @param clazz enumeration class
     * @param name name of enumeration item (case insensitive)</li> a * @return an instance of the given enumeration
     *        class or <code>null</code> if an appropriate item cannot be found
     */
    public static <T extends Enum<T>> T valueOf(final Class<T> clazz, final String name) {
        final T[] values = clazz.getEnumConstants();
        if (LengthUtils.isNotEmpty(values)) {
            for (final T item : values) {
                if (item.name().equalsIgnoreCase(name)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * This class represent unknown constant.
     */
    private static final class Unknown implements IntConstant {

        /**
         * Constant value.
         */
        private final int value;

        /**
         * Constructor.
         * 
         * @param value constant value
         */
        private Unknown(final int value) {
            this.value = value;
        }

        /**
         * @return the constant name
         * @see org.ak2.utils.enums.IntConstant#name()
         */
        public String name() {
            return code();
        }

        /**
         * @return the constant value
         * @see org.ak2.utils.enums.IntConstant#value()
         */
        public int value() {
            return value;
        }

        /**
         * @return the constant string value
         * @see org.ak2.utils.enums.IntConstant#code()
         */
        public String code() {
            return "" + value;
        }

        /**
         * @return the constant name
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return name();
        }

        /**
         * Returns a hash code value for the object.
         * 
         * @return int
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return value % 31;
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * 
         * @param obj the reference object with which to compare.
         * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Unknown other = (Unknown) obj;
            return value == other.value;
        }

    }

}