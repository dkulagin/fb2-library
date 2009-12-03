package org.ak2.utils;

/**
 * @author Alexander Kasatkin
 */
public final class RegexpUtils {

    /**
     * FAke constructor.
     */
    private RegexpUtils() {
    }

    /**
     * Make regexp for the given date-time format string.
     * 
     * @param dateFormat date-time format string
     * @return regexp compatible string
     * @see java.text.MessageFormat
     * @see java.util.regex.Pattern
     */
    public static String getDateTimeRegexp(final String dateFormat) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0, n = LengthUtils.length(dateFormat); i < n; i++) {
            final char c = dateFormat.charAt(i);
            switch (c) {
            case '.':
            case '[':
            case ']':
            case '-':
            case ' ':
            case '^':
            case '(':
            case ')':
            case '$':
            case '\\':
            case '/':
                buf.append('\\');
                buf.append(c);
                break;
            case 'd':
            case 'M':
            case 'y':
            case 'H':
            case 'm':
            case 's':
            case 'S':
                buf.append("\\d");
                break;
            case '?':
                buf.append(".?");
                break;
            case '*':
                buf.append(".*");
                break;
            default:
                buf.append(c);
                break;
            }
        }
        return buf.toString();
    }

    /**
     * Escapes Java Regexp valuable symbols.
     * 
     * @param name original name
     * @return escaped string
     */
    public static String getNameRegexp(final String name) {
        final StringBuilder buf = new StringBuilder();
        boolean escaped = false;
        for (int i = 0, n = LengthUtils.length(name); i < n; i++) {
            final char c = name.charAt(i);
            switch (c) {
            case '.':
            case '[':
            case ']':
            case '-':
            case ' ':
            case '^':
            case '(':
            case ')':
            case '$':
            case '/':
                buf.append('\\');
                buf.append(c);
                escaped = false;
                break;
            case '\\':
                if (!escaped) {
                    escaped = true;
                } else {
                    buf.append('\\');
                    buf.append(c);
                    escaped = false;
                }
                break;
            case '?':
                if (escaped) {
                    escaped = false;
                    buf.append("\\?");
                } else {
                    buf.append(".?");
                }
                break;
            case '*':
                if (escaped) {
                    escaped = false;
                    buf.append("\\*");
                } else {
                    buf.append(".*");
                }
                break;
            default:
                buf.append(c);
                escaped = false;
                break;
            }
        }
        return buf.toString();
    }

}
