package org.ak2.utils.collections.factories;

/**
 * @author Alexander Kasatkin
 */
public interface IMapValueFactory<K, V> {

    /**
     * IMapValueFactory method.
     * 
     * @param key map key
     * @return map value
     */
    V create(final K key);
}