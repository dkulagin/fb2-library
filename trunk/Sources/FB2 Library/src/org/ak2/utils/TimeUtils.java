package org.ak2.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Alexander Kasatkin
 */
public final class TimeUtils {

    /**
     * Fake constructor.
     */
    private TimeUtils() {
    }

    /**
     * Returns a day time interval for the given time value.
     * 
     * @param date datetime value
     * @return an instance of the {@link TimeInterval} object
     */
    public static TimeInterval day(final Date date) {
        final Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        final Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        end.add(Calendar.DAY_OF_YEAR, 1);

        return new TimeInterval(start, end);
    }

    /**
     * Returns a yesterday date-time value.
     * 
     * @param date date-time value
     * @return an instance of the {@link Date} object
     */
    public static Date yesterday(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, -1);
        return c.getTime();
    }

    /**
     * Calculates a difference between the given time values
     * 
     * @param start first time value
     * @param end second time value
     * @return difference in mills
     */
    public static long diff(final Date start, final Date end) {
        final long time1 = end.getTime();
        final long time2 = start.getTime();

        final long startTime = Math.min(time1, time2);
        final long endTime = Math.max(time1, time2);
        return endTime - startTime;
    }

    /**
     * This class implements a time interval.
     */
    public static class TimeInterval {

        /**
         * Start of interval (inclusive).
         */
        private final long m_start;

        /**
         * End of interval (exclusive).
         */
        private final long m_end;

        /**
         * Constructor.
         * 
         * @param start start time in mills
         * @param end end time in mills
         */
        public TimeInterval(final long start, final long end) {
            m_start = Math.min(start, end);
            m_end = Math.max(start, end);
        }

        /**
         * Constructor.
         * 
         * @param start start time
         * @param end end time
         */
        public TimeInterval(final Calendar start, final Calendar end) {
            this(start.getTimeInMillis(), end.getTimeInMillis());
        }

        /**
         * Checks the given time.
         * 
         * @param time time to check
         * @return <code>true</code> if the given time is greater or equal to interval start time and less than interval
         *         end time.
         */
        public boolean include(final long time) {
            return (m_start <= time) && (time < m_end);
        }

        /**
         * Checks the given time.
         * 
         * @param time time to check
         * @return <code>true</code> if the given time is greater or equal to interval start time and less than interval
         *         end time.
         */
        public boolean include(final Date time) {
            return time != null ? include(time.getTime()) : false;
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
            if (obj instanceof TimeInterval) {
                final TimeInterval that = (TimeInterval) obj;
                return (m_start == that.m_start) && (m_end == that.m_end);
            }
            return false;
        }

        /**
         * Returns a hash code value for the object.
         * 
         * @return a hash code value for this object.
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return (int) (m_start % 31);
        }

        /**
         * Returns a string representation of the object
         * 
         * @return string
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "TimeInterval[" + m_start + ", " + m_end + "]";
        }
    }
}
