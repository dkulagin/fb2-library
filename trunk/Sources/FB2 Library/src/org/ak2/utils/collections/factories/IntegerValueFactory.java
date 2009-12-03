package org.ak2.utils.collections.factories;

/**
 * @author Alexander Kasatkin
 */
public class IntegerValueFactory<K> implements IMapValueFactory<K, Integer> {

    /**
     * Default value.
     */
    private final int m_defValue;

    /**
     * Constructor.
     */
    public IntegerValueFactory() {
        this(0);
    }

    /**
     * Constructor.
     *
     * @param defValue default value
     */
    public IntegerValueFactory(final int defValue) {
        m_defValue = defValue;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.ak2.utils.collections.factories.IMapValueFactory#create(java.lang.Object)
     */
    public Integer create(final K key) {
        return Integer.valueOf(m_defValue);
    }
}