package org.ak2.utils.threadlocal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Alexander Kasatkin
 */
public class ThreadLocalPattern {

    /**
     * Regexp pattern.
     */
    private final String m_pattern;

    /**
     * Thread values.
     */
    private final ThreadLocal<Pattern> m_values = new ThreadLocal<Pattern>() {
        /**
         * {@inheritDoc}
         * 
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected Pattern initialValue() {
            return Pattern.compile(m_pattern);
        }
    };

    /**
     * Constructor.
     * 
     * @param pattern pattern string
     * @throws PatternSyntaxException the pattern syntax exception
     */
    public ThreadLocalPattern(String pattern) throws PatternSyntaxException {
        m_pattern = pattern;
        Pattern.compile(m_pattern);
    }

    /**
     * Creates a matcher that will match the given input against this pattern. </p>
     * 
     * @param input The character sequence to be matched
     * 
     * @return A new matcher for this pattern
     * 
     * @see Pattern#matcher(CharSequence)
     */
    public Matcher matcher(CharSequence input) {
        return m_values.get().matcher(input);
    }

}
