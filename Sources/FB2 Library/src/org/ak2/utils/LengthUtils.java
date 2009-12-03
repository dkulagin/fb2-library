package org.ak2.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author Alexander Kasatkin
 */
public final class LengthUtils {

    /** Empty string singleton. */
    private static final String SAFE_STRING = "";

    /**
     * Fake constructor.
     */
    protected LengthUtils() {
    }

    /**
     * Checks if the given string is empty.
     * 
     * @param s string
     * @return <code>true</code> if the given reference is <code>null</code> or string is empty
     */
    public static boolean isEmpty(final String s) {
        return length(s) == 0;
    }

    /**
     * Checks if any of given strings are empty.
     * 
     * @param strings strings to test
     * @return <code>true</code> if any of given strings are <code>null</code> or empty
     */
    public static boolean isAnyEmpty(final String... strings) {
        for (final String s : strings) {
            if (length(s) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all of given strings are empty.
     * 
     * @param strings strings to test
     * @return <code>true</code> if all of given strings are <code>null</code> or empty
     */
    public static boolean isAllEmpty(final String... strings) {
        for (final String s : strings) {
            if (length(s) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given string is not empty.
     * 
     * @param s string
     * @return <code>true</code> if the given reference is not <code>null</code> and string is not empty
     */
    public static boolean isNotEmpty(final String s) {
        return length(s) > 0;
    }

    /**
     * Checks if the given array is empty.
     * 
     * @param array array to check
     * @return <code>true</code> if the given reference is <code>null</code> or array is empty
     */
    public static boolean isEmpty(final Object[] array) {
        return length(array) == 0;
    }

    /**
     * Checks if the given array is empty.
     * 
     * @param array array to check
     * @return <code>true</code> if the given reference is <code>null</code> or array is empty
     */
    public static boolean isEmpty(final int[] array) {
        return length(array) == 0;
    }

    /**
     * Checks if the given list is empty.
     * 
     * @param list list to check
     * @return <code>true</code> if the given reference is <code>null</code> or list is empty
     */
    public static boolean isEmpty(final Collection<?> list) {
        return length(list) == 0;
    }

    /**
     * Checks if the given map is empty.
     * 
     * @param map map to check
     * @return <code>true</code> if the given reference is <code>null</code> or map is empty
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        return length(map) == 0;
    }

    /**
     * Checks if the given array is not empty.
     * 
     * @param array array to check
     * @return <code>true</code> if the given reference is not <code>null</code> and array is not empty
     */
    public static boolean isNotEmpty(final byte[] array) {
        return length(array) > 0;
    }

    /**
     * Checks if the given array is not empty.
     * 
     * @param array array to check
     * @return <code>true</code> if the given reference is not <code>null</code> and array is not empty
     */
    public static boolean isNotEmpty(final int[] array) {
        return length(array) > 0;
    }

    /**
     * Checks if the given array is not empty.
     * 
     * @param array array to check
     * @return <code>true</code> if the given reference is not <code>null</code> and array is not empty
     */
    public static boolean isNotEmpty(final Object[] array) {
        return length(array) > 0;
    }

    /**
     * Checks if the given list is not empty.
     * 
     * @param list list to check
     * @return <code>true</code> if the given reference is not <code>null</code> and list is not empty
     */
    public static boolean isNotEmpty(final Collection<?> list) {
        return length(list) > 0;
    }

    /**
     * Checks if the given map is not empty.
     * 
     * @param map map to check
     * @return <code>true</code> if the given reference is not <code>null</code> and map is not empty
     */
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return length(map) > 0;
    }

    /**
     * Safely calculates a string length.
     * 
     * @param s string
     * @return real string length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final String s) {
        return s != null ? s.length() : 0;
    }

    /**
     * Safely calculates an array length.
     * 
     * @param arr array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final Object[] arr) {
        return arr != null ? arr.length : 0;
    }

    /**
     * Safely calculates a list length.
     * 
     * @param list list to check
     * @return real list length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final Collection<?> list) {
        return list != null ? list.size() : 0;
    }

    /**
     * Safely calculates a map length.
     * 
     * @param map map to check
     * @return real map length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final Map<?, ?> map) {
        return map != null ? map.size() : 0;
    }

    /**
     * Safely calculates an array length.
     * 
     * @param arr array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final byte[] arr) {
        return arr != null ? arr.length : 0;
    }

    /**
     * Safely calculates an array length.
     * 
     * @param arr array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final int[] arr) {
        return arr != null ? arr.length : 0;
    }

    /**
     * Safely calculates an array length.
     * 
     * @param arr array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
    public static int length(final double[] arr) {
        return arr != null ? arr.length : 0;
    }

    /**
     * Returns empty string if the original one is null.
     * 
     * @param original original string
     * @return string
     */
    public static String unsafeString(final String original) {
        return length(original) == 0 ? null : original;
    }

    /**
     * Returns empty string if the original one is null.
     * 
     * @param original original string
     * @return string
     */
    public static String safeString(final String original) {
        return safeString(original, safeString());
    }

    /**
     * Returns default string if the original one is empty.
     * 
     * @param original original string
     * @param defaultValue default string value
     * @return string
     */
    public static String safeString(final String original, final String defaultValue) {
        return isNotEmpty(original) ? original : defaultValue;
    }

    /**
     * Returns empty safe string.
     * 
     * @return a safe empty string
     */
    public static String safeString() {
        return SAFE_STRING;
    }

    /**
     * Compares two strings.
     * 
     * @param s1 first string
     * @param s2 second string
     * @return if strings are equal or both are null
     */
    public static boolean equals(final String s1, final String s2) {
        if (isEmpty(s1)) {
            return isEmpty(s2) ? true : false;
        }

        return s1.equals(s2);
    }

    /**
     * Compares two arrays of strings.
     * 
     * @param s1 first string array
     * @param s2 second string array
     * @return if strings are equal or both are null
     */
    public static boolean equals(final String[] s1, final String[] s2) {
        final int length1 = length(s1);
        final int length2 = length(s2);

        if (length1 != length2) {
            return false;
        }

        for (int i = 0; i < length1; i++) {
            if (!equals(s1[i], s2[2])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two strings ignoring char cases.
     * 
     * @param s1 first string
     * @param s2 second string
     * @return if strings are equal or both are null
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        if (isEmpty(s1)) {
            return isEmpty(s2) ? true : false;
        }

        return s1.equalsIgnoreCase(s2);
    }

    /**
     * Compares two objects.
     * 
     * @param o1 first object
     * @param o2 second object
     * @return if objects are equal or both are null
     */
    public static boolean equals(final Object o1, final Object o2) {
        if (o1 == null) {
            return o2 == null ? true : false;
        }

        return o1.equals(o2);
    }

    /**
     * Converts objects to array.
     * 
     * @param <T> type of objects in the array
     * @param objects objects to converts
     * @return array
     */
    public static <T> T[] toArray(final T... objects) {
        return objects;
    }

    /**
     * Converts object to string.
     * 
     * @param obj object
     * @return string
     */
    public static String toString(final Object obj) {
        return obj != null ? obj.toString() : safeString();
    }

    /**
     * Returns a hash code value for the object.
     * 
     * @param obj the obj
     * @return the int
     */
    public static int hashCode(final Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }
}
