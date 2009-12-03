package org.ak2.utils.collections.factories;

/**
 * @author Alexander Kasatkin
 */
public class DefaultMapValueFactory<K, V> implements IMapValueFactory<K, V> {

    /**
     * {@inheritDoc}
     * 
     * @see org.ak2.utils.collections.factories.IMapValueFactory#create(java.lang.Object)
     */
    public V create(K key) {
        return null;
    }

    /**
     * Wraps the <code>null</code> reference with instance of the {@link DefaultMapValueFactory} object.
     * 
     * @param <K> key type
     * @param <V> value type
     * @param factory original factory
     * @return an instance of the {@link DefaultMapValueFactory} object
     */
    public static <K, V> IMapValueFactory<K, V> wrapFactory(IMapValueFactory<K, V> factory) {
        return factory != null ? factory : new DefaultMapValueFactory<K, V>();
    }

}