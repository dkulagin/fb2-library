package org.ak2.utils;

/**
 * @author Alexander Kasatkin
 */
public final class CompareUtils {

    /**
     * Fake constructor.
     */
    private CompareUtils() {
    }

    /**
     * Compare two boolean values.
     * 
     * @param val1 first value
     * @param val2 second value
     * @return on of the following values:
     *         <ul>
     *         <li><code>-1</code> if the first value is <code>false</code> and the second one is <code>true</code></li>
     *         <li><code>0</code> if both values are equal</li>
     *         <li><code>1</code> if the first value is <code>true</code> and the second one is <code>false</code></li>
     *         </ul>
     */
    public static int compare(final boolean val1, final boolean val2) {
        return compare(val1 ? 1 : 0, val2 ? 1 : 0);
    }

    /**
     * Compare two integer values.
     * 
     * @param val1 first value
     * @param val2 second value
     * @return on of the following values:
     *         <ul>
     *         <li><code>-1</code> if the first value is less than the second one</li>
     *         <li><code>0</code> if both values are equal</li>
     *         <li><code>-1</code> if the first value is greater than the second one</li>
     *         </ul>
     */
    public static int compare(final int val1, final int val2) {
        return val1 < val2 ? -1 : val1 > val2 ? 1 : 0;
    }

    /**
     * Compare two long values.
     * 
     * @param val1 first value
     * @param val2 second value
     * @return on of the following values:
     *         <ul>
     *         <li><code>-1</code> if the first value is less than the second one</li>
     *         <li><code>0</code> if both values are equal</li>
     *         <li><code>-1</code> if the first value is greater than the second one</li>
     *         </ul>
     */
    public static int compare(final long val1, final long val2) {
        return val1 < val2 ? -1 : val1 > val2 ? 1 : 0;
    }
}
