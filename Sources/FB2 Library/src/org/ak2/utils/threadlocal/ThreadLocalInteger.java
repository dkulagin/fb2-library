package org.ak2.utils.threadlocal;

/**
 * @author Alexander Kasatkin
 */
public class ThreadLocalInteger extends Number {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1115358453594718584L;

    /**
     * Initial value.
     */
    private final int m_initial;

    /**
     * Thread values.
     */
    private final ThreadLocal<Integer> m_values = new ThreadLocal<Integer>() {
        /**
         * {@inheritDoc}
         * 
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected Integer initialValue() {
            return m_initial;
        }
    };

    /**
     * Constructor.
     */
    public ThreadLocalInteger() {
        this(0);
    }

    /**
     * Constructor.
     * 
     * @param initial initial value
     */
    public ThreadLocalInteger(int initial) {
        m_initial = initial;
    }

    /**
     * Get the current value.
     * 
     * @return the current value
     */
    public final int get() {
        return m_values.get();
    }

    /**
     * Set to the given value.
     * 
     * @param newValue the new value
     */
    public final void set(int newValue) {
        m_values.set(newValue);
    }

    /**
     * Set to the give value and return the old value.
     * 
     * @param newValue the new value
     * @return the previous value
     */
    public final int getAndSet(int newValue) {
        int current = m_values.get();
        m_values.set(newValue);
        return current;
    }

    /**
     * Atomically set the value to the given updated value if the current value <tt>==</tt> the expected value.
     * 
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int expect, int update) {
        if (expect == get()) {
            set(update);
            return true;
        }
        return false;
    }

    /**
     * Atomically increment by one the current value.
     * 
     * @return the previous value
     */
    public final int getAndIncrement() {
        int current = m_values.get();
        m_values.set(current + 1);
        return current;
    }

    /**
     * Atomically decrement by one the current value.
     * 
     * @return the previous value
     */
    public final int getAndDecrement() {
        int current = m_values.get();
        m_values.set(current - 1);
        return current;
    }

    /**
     * Atomically add the given value to current value.
     * 
     * @param delta the value to add
     * @return the previous value
     */
    public final int getAndAdd(int delta) {
        int current = m_values.get();
        m_values.set(current + delta);
        return current;
    }

    /**
     * Atomically increment by one the current value.
     * 
     * @return the updated value
     */
    public final int incrementAndGet() {
        int current = m_values.get() + 1;
        m_values.set(current);
        return current;
    }

    /**
     * Atomically decrement by one the current value.
     * 
     * @return the updated value
     */
    public final int decrementAndGet() {
        int current = m_values.get() - 1;
        m_values.set(current);
        return current;
    }

    /**
     * Atomically add the given value to current value.
     * 
     * @param delta the value to add
     * @return the updated value
     */
    public final int addAndGet(int delta) {
        int current = m_values.get() + delta;
        m_values.set(current);
        return current;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public final double doubleValue() {
        return (double) get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Number#floatValue()
     */
    @Override
    public final float floatValue() {
        return (float) get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Number#intValue()
     */
    @Override
    public final int intValue() {
        return get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Number#longValue()
     */
    @Override
    public final long longValue() {
        return (long) get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return Integer.toString(get());
    }
}
