package org.ak2.utils.threadlocal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alexander Kasatkin
 */
public class ThreadLocalDateFormat {

    /**
     * Format string.
     */
    private final String m_format;

    /**
     * Thread formats.
     */
    private final ThreadLocal<SimpleDateFormat> m_values = new ThreadLocal<SimpleDateFormat>() {
        /**
         * {@inheritDoc}
         * 
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(m_format);
        }
    };

    /**
     * Constructor.
     * 
     * @param format date-time format
     * @exception IllegalArgumentException if the given pattern is invalid
     */
    public ThreadLocalDateFormat(String format) throws IllegalArgumentException {
        m_format = format;
        new SimpleDateFormat(m_format);
    }

    /**
     * Formats a Date into a date/time string.
     * 
     * @param date the time value to be formatted into a time string.
     * @return the formatted time string.
     */
    public String format(Date date) {
        return m_values.get().format(date);
    }

    /**
     * Parses text from the beginning of the given string to produce a date. The method may not use the entire text of
     * the given string.
     * <p>
     * 
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return A <code>Date</code> parsed from the string.
     * @exception ParseException if the beginning of the specified string cannot be parsed.
     */
    public Date parse(final String source) throws ParseException {
        return m_values.get().parse(source);
    }
}
