package org.ak2.utils.threadlocal;

/**
 * @author Alexander Kasatkin
 */
public class ThreadLocalBuffer {

    /**
     * Default buffer size
     */
    private final int m_defaultSize;

    /**
     * Thread buffers.
     */
    private final ThreadLocal<StringBuilder> m_buffers = new ThreadLocal<StringBuilder>() {
        /**
         * {@inheritDoc}
         * 
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(m_defaultSize);
        }
    };

    /**
     * Constructor.
     */
    public ThreadLocalBuffer() {
        this(256);
    }

    /**
     * Constructor.
     * 
     * @param defaultSize default buffer size
     */
    public ThreadLocalBuffer(final int defaultSize) {
        m_defaultSize = defaultSize;
        m_buffers.get();
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param b value to append
     * @return this
     * @see java.lang.StringBuilder#append(boolean)
     */
    public StringBuilder append(boolean b) {
        return m_buffers.get().append(b);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param c value to append
     * @return this
     * @see java.lang.StringBuilder#append(char)
     */
    public StringBuilder append(char c) {
        return m_buffers.get().append(c);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param str the characters to be appended.
     * @param offset the index of the first <code>char</code> to append.
     * @param len the number of <code>char</code>s to append.
     * @return this
     * @see java.lang.StringBuilder#append(char[], int, int)
     */
    public StringBuilder append(char[] str, int offset, int len) {
        return m_buffers.get().append(str, offset, len);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param str value to append
     * @return this
     * @see java.lang.StringBuilder#append(char[])
     */
    public StringBuilder append(char[] str) {
        return m_buffers.get().append(str);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param s the sequence to append.
     * @param start the starting index of the subsequence to be appended.
     * @param end the end index of the subsequence to be appended.
     * @return this
     * @see java.lang.StringBuilder#append(java.lang.CharSequence, int, int)
     */
    public StringBuilder append(CharSequence s, int start, int end) {
        return m_buffers.get().append(s, start, end);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param s value to append
     * @return this
     * @see java.lang.StringBuilder#append(java.lang.CharSequence)
     */
    public StringBuilder append(CharSequence s) {
        return m_buffers.get().append(s);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param d value to append
     * @return this
     * @see java.lang.StringBuilder#append(double)
     */
    public StringBuilder append(double d) {
        return m_buffers.get().append(d);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param f value to append
     * @return this
     * @see java.lang.StringBuilder#append(float)
     */
    public StringBuilder append(float f) {
        return m_buffers.get().append(f);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param i value to append
     * @return this
     * @see java.lang.StringBuilder#append(int)
     */
    public StringBuilder append(int i) {
        return m_buffers.get().append(i);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param lng value to append
     * @return this
     * @see java.lang.StringBuilder#append(long)
     */
    public StringBuilder append(long lng) {
        return m_buffers.get().append(lng);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param obj value to append
     * @return this
     * @see java.lang.StringBuilder#append(java.lang.Object)
     */
    public StringBuilder append(Object obj) {
        return m_buffers.get().append(obj);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param str value to append
     * @return this
     * @see java.lang.StringBuilder#append(java.lang.String)
     */
    public StringBuilder append(String str) {
        return m_buffers.get().append(str);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param sb value to append
     * @return this
     * @see java.lang.StringBuilder#append(java.lang.StringBuffer)
     */
    public StringBuilder append(StringBuffer sb) {
        return m_buffers.get().append(sb);
    }

    /**
     * Appends the given value to the buffer.
     * 
     * @param codePoint value to append
     * @return this
     * @see java.lang.StringBuilder#appendCodePoint(int)
     */
    public StringBuilder appendCodePoint(int codePoint) {
        return m_buffers.get().appendCodePoint(codePoint);
    }

    /**
     * Returns the current buffer capacity
     * 
     * @return the current buffer capacity
     * @see java.lang.AbstractStringBuilder#capacity()
     */
    public int capacity() {
        return m_buffers.get().capacity();
    }

    /**
     * Returns the character (Unicode code point) at the specified index.
     * 
     * @param index character index
     * @return character
     * @see java.lang.AbstractStringBuilder#charAt(int)
     */
    public char charAt(int index) {
        return m_buffers.get().charAt(index);
    }

    /**
     * Returns character at the given index
     * 
     * @param index character index
     * @return character
     * @see java.lang.AbstractStringBuilder#codePointAt(int)
     */
    public int codePointAt(int index) {
        return m_buffers.get().codePointAt(index);
    }

    /**
     * Returns the character (Unicode code point) before the specified index.
     * 
     * @param index the index following the code point that should be returned
     * @return the Unicode code point value before the given index.
     * @see java.lang.AbstractStringBuilder#codePointBefore(int)
     */
    public int codePointBefore(int index) {
        return m_buffers.get().codePointBefore(index);
    }

    /**
     * Returns the number of Unicode code points in the specified text range of this sequence.
     * 
     * @param beginIndex the index to the first <code>char</code> of the text range.
     * @param endIndex the index after the last <code>char</code> of the text range.
     * @return the number of Unicode code points in the specified text range
     * @see java.lang.AbstractStringBuilder#codePointCount(int, int)
     */
    public int codePointCount(int beginIndex, int endIndex) {
        return m_buffers.get().codePointCount(beginIndex, endIndex);
    }

    /**
     * Removes the characters in a substring of this sequence.
     * 
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @return this
     * @see java.lang.StringBuilder#delete(int, int)
     */
    public StringBuilder delete(int start, int end) {
        return m_buffers.get().delete(start, end);
    }

    /**
     * Removes the <code>char</code> at the specified position in this sequence.
     * 
     * @param index Index of <code>char</code> to remove
     * @return this
     * @see java.lang.StringBuilder#deleteCharAt(int)
     */
    public StringBuilder deleteCharAt(int index) {
        return m_buffers.get().deleteCharAt(index);
    }

    /**
     * Ensures that the capacity is at least equal to the specified minimum.
     * 
     * @param minimumCapacity the minimum desired capacity.
     * @see java.lang.AbstractStringBuilder#ensureCapacity(int)
     */
    public void ensureCapacity(int minimumCapacity) {
        m_buffers.get().ensureCapacity(minimumCapacity);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return m_buffers.get().equals(obj);
    }

    /**
     * Characters are copied from this sequence into the destination character array <code>dst</code>.
     * 
     * @param srcBegin start copying at this offset.
     * @param srcEnd stop copying at this offset.
     * @param dst the array to copy the data into.
     * @param dstBegin offset into <code>dst</code>.
     * @see java.lang.AbstractStringBuilder#getChars(int, int, char[], int)
     */
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        m_buffers.get().getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return m_buffers.get().hashCode();
    }

    /**
     * This buffer is the character array being searched, and the str is the string being searched for.
     * 
     * @param str the characters being searched for.
     * @param fromIndex the index to begin searching from.
     * @return index of found substring or <code>-1</code>
     * @see java.lang.StringBuilder#indexOf(java.lang.String, int)
     */
    public int indexOf(String str, int fromIndex) {
        return m_buffers.get().indexOf(str, fromIndex);
    }

    /**
     * This buffer is the character array being searched, and the str is the string being searched for.
     * 
     * @param str the characters being searched for.
     * @return index of found substring or <code>-1</code>
     * @see java.lang.StringBuilder#indexOf(java.lang.String)
     */
    public int indexOf(String str) {
        return m_buffers.get().indexOf(str);
    }

    /**
     * Inserts the string representation of the <code>boolean</code> argument into this sequence.
     * 
     * @param offset the offset.
     * @param b a <code>boolean</code>.
     * @return this
     * @see java.lang.StringBuilder#insert(int, boolean)
     */
    public StringBuilder insert(int offset, boolean b) {
        return m_buffers.get().insert(offset, b);
    }

    /**
     * Inserts the string representation of the <code>char</code> argument into this sequence.
     * 
     * @param offset the offset.
     * @param c a <code>char</code>.
     * @return this
     * @see java.lang.StringBuilder#insert(int, char)
     */
    public StringBuilder insert(int offset, char c) {
        return m_buffers.get().insert(offset, c);
    }

    /**
     * Inserts the string representation of a subarray of the <code>str</code> array argument into this sequence.
     * 
     * @param index position at which to insert subarray.
     * @param str A <code>char</code> array.
     * @param offset the index of the first <code>char</code> in subarray to be inserted.
     * @param len the number of <code>char</code>s in the subarray to be inserted.
     * @return this
     * @see java.lang.StringBuilder#insert(int, char[], int, int)
     */
    public StringBuilder insert(int index, char[] str, int offset, int len) {
        return m_buffers.get().insert(index, str, offset, len);
    }

    /**
     * Inserts the string representation of the <code>char</code> array argument into this sequence.
     * 
     * @param offset the offset.
     * @param str a character array.
     * @return this
     * @see java.lang.StringBuilder#insert(int, char[])
     */
    public StringBuilder insert(int offset, char[] str) {
        return m_buffers.get().insert(offset, str);
    }

    /**
     * Inserts a subsequence of the specified <code>CharSequence</code> into this sequence.
     * 
     * @param dstOffset the offset in this sequence.
     * @param s the sequence to be inserted.
     * @param start the starting index of the subsequence to be inserted.
     * @param end the end index of the subsequence to be inserted.
     * @return this
     * @see java.lang.StringBuilder#insert(int, java.lang.CharSequence, int, int)
     */
    public StringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        return m_buffers.get().insert(dstOffset, s, start, end);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param dstOffset the offset.
     * @param s value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, java.lang.CharSequence)
     */
    public StringBuilder insert(int dstOffset, CharSequence s) {
        return m_buffers.get().insert(dstOffset, s);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param offset the offset.
     * @param d value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, double)
     */
    public StringBuilder insert(int offset, double d) {
        return m_buffers.get().insert(offset, d);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param offset the offset.
     * @param f value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, float)
     */
    public StringBuilder insert(int offset, float f) {
        return m_buffers.get().insert(offset, f);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param offset the offset.
     * @param i value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, int)
     */
    public StringBuilder insert(int offset, int i) {
        return m_buffers.get().insert(offset, i);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param offset the offset.
     * @param l value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, long)
     */
    public StringBuilder insert(int offset, long l) {
        return m_buffers.get().insert(offset, l);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param offset the offset.
     * @param obj value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, java.lang.Object)
     */
    public StringBuilder insert(int offset, Object obj) {
        return m_buffers.get().insert(offset, obj);
    }

    /**
     * Inserts the given value into this buffer.
     * 
     * @param offset the offset.
     * @param str value to insert.
     * @return this
     * @see java.lang.StringBuilder#insert(int, java.lang.String)
     */
    public StringBuilder insert(int offset, String str) {
        return m_buffers.get().insert(offset, str);
    }

    /**
     * This buffer is the character array being searched, and the str is the string being searched for.
     * 
     * @param str the characters being searched for.
     * @param fromIndex the index to begin searching from.
     * @return this
     * @see java.lang.StringBuilder#lastIndexOf(java.lang.String, int)
     */
    public int lastIndexOf(String str, int fromIndex) {
        return m_buffers.get().lastIndexOf(str, fromIndex);
    }

    /**
     * This buffer is the character array being searched, and the str is the string being searched for.
     * 
     * @param str the characters being searched for.
     * @return this
     * @see java.lang.StringBuilder#lastIndexOf(java.lang.String)
     */
    public int lastIndexOf(String str) {
        return m_buffers.get().lastIndexOf(str);
    }

    /**
     * Returns the length (character count).
     * 
     * @return the length of the sequence of characters currently represented by this object
     * @see java.lang.AbstractStringBuilder#length()
     */
    public int length() {
        return m_buffers.get().length();
    }

    /**
     * Returns the index within this sequence that is offset from the given <code>index</code> by
     * <code>codePointOffset</code> code points. Unpaired surrogates within the text range given by <code>index</code>
     * and <code>codePointOffset</code> count as one code point each.
     * 
     * @param index the index to be offset
     * @param codePointOffset the offset in code points
     * @return the index within this buffer
     * @see java.lang.AbstractStringBuilder#offsetByCodePoints(int, int)
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        return m_buffers.get().offsetByCodePoints(index, codePointOffset);
    }

    /**
     * Replaces the characters in a substring of this sequence with characters in the specified <code>String</code>.
     * 
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @param str String that will replace previous contents.
     * @return this
     * @see java.lang.StringBuilder#replace(int, int, java.lang.String)
     */
    public StringBuilder replace(int start, int end, String str) {
        return m_buffers.get().replace(start, end, str);
    }

    /**
     * Causes this character sequence to be replaced by the reverse of the sequence.
     * 
     * @return this
     * @see java.lang.StringBuilder#reverse()
     */
    public StringBuilder reverse() {
        return m_buffers.get().reverse();
    }

    /**
     * The character at the specified index is set to <code>ch</code>.
     * 
     * @param index the index of the character to modify.
     * @param ch the new character.
     * @see java.lang.AbstractStringBuilder#setCharAt(int, char)
     */
    public void setCharAt(int index, char ch) {
        m_buffers.get().setCharAt(index, ch);
    }

    /**
     * Sets the length of the character sequence.
     * 
     * @param newLength the new length
     * @see java.lang.AbstractStringBuilder#setLength(int)
     */
    public void setLength(int newLength) {
        m_buffers.get().setLength(newLength);
    }

    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     * 
     * @param start the start index, inclusive.
     * @param end the end index, exclusive.
     * @return the specified subsequence.
     * @see java.lang.AbstractStringBuilder#subSequence(int, int)
     */
    public CharSequence subSequence(int start, int end) {
        return m_buffers.get().subSequence(start, end);
    }

    /**
     * Returns a new <code>String</code> that contains a subsequence of characters currently contained in this sequence.
     * 
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @return The new string.
     * @see java.lang.AbstractStringBuilder#substring(int, int)
     */
    public String substring(int start, int end) {
        return m_buffers.get().substring(start, end);
    }

    /**
     * Returns a new <code>String</code> that contains a subsequence of characters currently contained in this character
     * sequence.
     * 
     * @param start The beginning index, inclusive.
     * @return The new string.
     * @see java.lang.AbstractStringBuilder#substring(int)
     */
    public String substring(int start) {
        return m_buffers.get().substring(start);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.StringBuilder#toString()
     */
    public String toString() {
        return m_buffers.get().toString();
    }

    /**
     * Attempts to reduce storage used for the character sequence.
     * 
     * @see java.lang.AbstractStringBuilder#trimToSize()
     */
    public void trimToSize() {
        m_buffers.get().trimToSize();
    }

    /**
     * @return the thread-local buffer instance
     */
    public StringBuilder buffer() {
        return m_buffers.get();
    }

}
