package org.ak2.fb2.export.palmdoc.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class PalmDate {

    private static final long serialVersionUID = 2772773007534517461L;

    private static final Calendar CALENDAR = new GregorianCalendar();

    private static final long SecondsSince1904 = 0x7c25b080L;

    private PalmDate() {
    }

    public static int getPalmDate(final Date date) {
        return (int) ((date.getTime() + offset()) / 1000L + SecondsSince1904);
    }

    public static long getDate(final int palmDate) {
        long longValue = (new Integer(palmDate)).longValue();
        if (longValue < 0L) {
            longValue += 0x100000000L;
        }
        return (longValue - SecondsSince1904) * 1000L - offset();
    }

    private static long offset() {
        return (long) CALENDAR.get(Calendar.ZONE_OFFSET) + (long) CALENDAR.get(Calendar.DST_OFFSET);
    }
}
