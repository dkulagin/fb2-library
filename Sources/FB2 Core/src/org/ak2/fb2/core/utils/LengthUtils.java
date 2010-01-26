package org.ak2.fb2.core.utils;

import java.util.List;

/**
 * @author Alexander Kasatkin
 */
public final class LengthUtils {

	private static final String SAFE_STRING = "";

	/**
     * Constructor.
     */
	private LengthUtils() {
	}

	/**
     * @param s
     *            string
     * @return <code>true</code> if the given reference is <code>null</code> or string is empty
     */
	public static boolean isEmpty(final String s) {
		return length(s) == 0;
	}

	/**
     * @param s
     *            string
     * @return <code>true</code> if the given reference is not <code>null</code> and string is not empty
     */
	public static boolean isNotEmpty(final String s) {
		return length(s) > 0;
	}

	/**
     * @param array
     *            array to check
     * @return <code>true</code> if the given reference is <code>null</code> or array is empty
     */
	public static boolean isEmpty(final Object[] array) {
		return length(array) == 0;
	}

	/**
     * @param list
     *            list to check
     * @return <code>true</code> if the given reference is <code>null</code> or list is empty
     */
	public static boolean isEmpty(final List<?> list) {
		return length(list) == 0;
	}

	/**
     * @param array
     *            array to check
     * @return <code>true</code> if the given reference is not <code>null</code> and array is not empty
     */
	public static boolean isNotEmpty(final int[] array) {
		return length(array) > 0;
	}

	/**
     * @param array
     *            array to check
     * @return <code>true</code> if the given reference is not <code>null</code> and array is not empty
     */
	public static boolean isNotEmpty(final Object[] array) {
		return length(array) > 0;
	}

	/**
     * @param list
     *            list to check
     * @return <code>true</code> if the given reference is not <code>null</code> and list is not empty
     */
	public static boolean isNotEmpty(final List<?> list) {
		return length(list) > 0;
	}

	/**
     * @param s
     *            string
     * @return real string length or <code>0</code> if reference is <code>null</code>
     */
	public static int length(final String s) {
		return s != null ? s.length() : 0;
	}

	/**
     * @param arr
     *            array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
	public static int length(final Object[] arr) {
		return arr != null ? arr.length : 0;
	}

	/**
     * @param list
     *            list to check
     * @return real list length or <code>0</code> if reference is <code>null</code>
     */
	public static int length(final List<?> list) {
		return list != null ? list.size() : 0;
	}

	/**
     * @param arr
     *            array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
	public static int length(final int[] arr) {
		return arr != null ? arr.length : 0;
	}

	/**
     * @param arr
     *            array
     * @return real array length or <code>0</code> if reference is <code>null</code>
     */
	public static int length(final double[] arr) {
		return arr != null ? arr.length : 0;
	}

	/**
     * Returns empty string if the original one is null
     *
     * @param original
     *            original string
     * @return string
     */
	public static String safeString(final String original) {
		return safeString(original, safeString());
	}

	/**
     * Returns empty string if the original one is null
     *
     * @param original
     *            original string
     * @param defaultValue
     *            default string value
     * @return string
     */
	public static String safeString(final String original, final String defaultValue) {
		return isNotEmpty(original) ? original : defaultValue;
	}

	/**
     * @return a safe empty string
     */
	public static String safeString() {
		return SAFE_STRING;
	}

	/**
     * Compares two strings.
     *
     * @param s1
     *            first string
     * @param s2
     *            second string
     * @return if strings are equal or both are null
     */
	public static boolean equals(final String s1, final String s2) {
		if (isEmpty(s1)) {
			return isEmpty(s2) ? true : false;
		}

		return s1.equals(s2);
	}

	/**
     * Compares two objects.
     *
     * @param o1
     *            first object
     * @param o2
     *            second isEmpty(s1)
     * @return if objects are equal or both are null
     */
	public static boolean equals(final Object o1, final Object o2) {
		if (o1 == null) {
			return o2 == null;
		}

		return o1.equals(o2);
	}

	/**
     * Converts object to string
     *
     * @param obj
     *            object
     * @return string
     */
	public static String toString(final Object obj) {
		return obj != null ? obj.toString() : safeString();
	}
}
