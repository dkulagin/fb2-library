package org.ak2.utils.csv;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public class CsvBuilder {

    /**
     * Default field separator.
     */
    static final String DEF_FIELD_SEPARATOR = ",";

    /**
     * Internal buffer
     */
    private final StringBuilder buf = new StringBuilder();

    /**
     * Field separator.
     */
    private String m_fieldSeparator = DEF_FIELD_SEPARATOR;

    private boolean m_newLine = true;

    /**
     * Constructor.
     */
    public CsvBuilder() {
    }

    /**
     * Constructor.
     *
     * @param fieldSeparator
     *            field separator
     */
    public CsvBuilder(final String fieldSeparator) {
        m_fieldSeparator = fieldSeparator;
    }

    /**
     * Adds the given fields into a row.
     *
     * @param fields
     *            fields to add
     * @return this
     */
    public CsvBuilder add(final Object... fields) {
        for (final Object field : fields) {
            addField(field);
        }
        return this;
    }

    /**
     * Adds the given fields into a row.
     *
     * @param fields
     *            fields to add
     * @return this
     */
    public CsvBuilder add(final Iterable<?> fields) {
        for (final Object field : fields) {
            addField(field);
        }
        return this;
    }

    /**
     * Adds the given field to a row.
     *
     * @param field
     *            field to add
     * @return this
     */
    public CsvBuilder addField(final Object field) {
        if (m_newLine) {
            m_newLine = false;
        } else {
            buf.append(m_fieldSeparator);
        }
        final String string = LengthUtils.toString(field);
        if (LengthUtils.isNotEmpty(string)) {
            if (string.indexOf(m_fieldSeparator) != -1 && !string.startsWith("\"")) {
                buf.append("\"").append(string).append("\"");
            } else {
                buf.append(string);
            }
        }
        return this;
    }

    /**
     * Merges the given parts and adds the result as a new field.
     *
     * @param sep
     *            parts separator
     * @param parts
     *            field parts
     * @return this
     */
    public CsvBuilder merge(final String sep, final Iterable<?> parts) {
        StringBuilder buf = new StringBuilder();
        boolean added = false;
        for (final Object part : parts) {
            final String string = LengthUtils.toString(part);
            if (LengthUtils.isNotEmpty(string)) {
                if (added) {
                    buf.append(sep);
                } else {
                    added = true;
                }
                buf.append(string);
            }
        }
        return this.addField(buf);
    }

    /**
     * Merges the given parts and adds the result as a new field.
     *
     * @param sep
     *            parts separator
     * @param parts
     *            field parts
     * @return this
     */
    public CsvBuilder merge(final String sep, final Object... parts) {
        StringBuilder buf = new StringBuilder();
        boolean added = false;
        for (final Object part : parts) {
            final String string = LengthUtils.toString(part);
            if (LengthUtils.isNotEmpty(string)) {
                if (added) {
                    buf.append(sep);
                } else {
                    added = true;
                }
                buf.append(string);
            }
        }
        return this.addField(buf);
    }

    /**
     * Finishes the current row with line end.
     *
     * @return this
     */
    public CsvBuilder eol() {
        buf.append("\n");
        m_newLine = true;
        return this;
    }

    /**
     * Clears the internal buffer.
     *
     * @return this
     */
    public CsvBuilder reset() {
        buf.setLength(0);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return buf.toString();
    }
}