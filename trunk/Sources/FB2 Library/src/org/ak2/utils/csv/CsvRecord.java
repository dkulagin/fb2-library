package org.ak2.utils.csv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ak2.utils.LengthUtils;

/**
 * @author Alexander Kasatkin
 */
public class CsvRecord implements Iterable<String> {

  /**
   * List of fields
   */
  private final List<String> m_fields = new ArrayList<String>();

  /**
   * Constructor.
   *
   * @param record
   *          original record
   */
  public CsvRecord(final CsvRecord record) {
    m_fields.addAll(record.m_fields);
  }

  /**
   * Constructor.
   *
   * @param line
   *          line read from CVS file
   */
  public CsvRecord(final String line) {
    this(line, CsvBuilder.DEF_FIELD_SEPARATOR, false);
  }

  /**
   * Constructor.
   *
   * @param line
   *          line text
   * @param fieldSeparator
   *          field separator
   * @param saveQuotes
   *          if <code>true</code> field quotes are saved in parsed fields
   */
  public CsvRecord(final String line, final String fieldSeparator, final boolean saveQuotes) {
    String str = line;
    while (LengthUtils.isNotEmpty(str)) {
      if (str.charAt(0) == '"') {
        int nextQuote = str.indexOf('"', 1);
        nextQuote = nextQuote == -1 ? str.length() : saveQuotes ? nextQuote + 1 : nextQuote;

        final String value = str.substring(saveQuotes ? 0 : 1, nextQuote);
        m_fields.add(value);

        str = str.substring(nextQuote + 1);
        final int nextSeparator = str.indexOf(fieldSeparator);
        if (nextSeparator != -1) {
          str = str.substring(nextSeparator + 1);
        }
      } else {
        final int nextSeparator = str.indexOf(fieldSeparator);
        if (nextSeparator != -1) {
          final String value = str.substring(0, nextSeparator);
          m_fields.add(value);
          str = str.substring(nextSeparator + 1);
        } else {
          m_fields.add(str);
          str = null;
        }
      }
    }
  }

  /**
   * @return number of fields
   */
  public int size() {
    return m_fields.size();
  }

  /**
   * Returns field value
   *
   * @param index
   *          field index
   * @return field value
   */
  public String getField(final int index) {
    return index < size() ? m_fields.get(index) : null;
  }

  /**
   * Sets field value.
   *
   * @param index
   *          field index
   * @param value
   *          value to set
   */
  public void setField(final int index, final String value) {
    if (index < size()) {
      m_fields.set(index, value);
    } else {
      m_fields.add(value);
    }
  }

  /**
   * Replaces all fields by the given value
   *
   * @param defaultValue
   *          value to set
   */
  public void reset(final String defaultValue) {
    for (int i = 0; i < size(); i++) {
      setField(i, defaultValue);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Iterable#iterator()
   */
  public Iterator<String> iterator() {
    return m_fields.iterator();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return m_fields.toString();
  }
}
