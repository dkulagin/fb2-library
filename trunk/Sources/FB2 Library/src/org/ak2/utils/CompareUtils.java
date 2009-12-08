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
    
    /**
     * Simple implementation of Levenstein distance algorithm.
     * @param s1 first string to compare
     * @param s2 second string to compare
     * @return
     */
    public static int levensteinDistance(final String s1, final String s2) {
    	int m = s1.length(), n = s2.length();
    	int[] D1 = new int[n + 1];
    	int[] D2 = new int[n + 1];
     
    	for(int i = 0; i <= n; i ++) {
    		D2[i] = i;
    	}
     
    	for(int i = 1; i <= m; i ++) {
    		D1 = D2;
    		D2 = new int[n + 1];
    		for(int j = 0; j <= n; j ++) {
    			if(j == 0) D2[j] = i;
    			else {
    				int cost = (s1.charAt(i - 1) != s2.charAt(j - 1)) ? 1 : 0;
    				if(D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost) {
    					D2[j] = D2[j - 1] + 1;
    				}
    				else if(D1[j] < D1[j - 1] + cost) {
    					D2[j] = D1[j] + 1;
    				}
    				else {
    					D2[j] = D1[j - 1] + cost;
    				}
    			}
    		}
    	}
    	return D2[n];
    }

}
