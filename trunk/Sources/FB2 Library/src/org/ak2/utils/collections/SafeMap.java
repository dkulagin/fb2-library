package org.ak2.utils.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ak2.utils.collections.factories.DefaultMapValueFactory;
import org.ak2.utils.collections.factories.IMapValueFactory;

/**
 * @author Alexander Kasatkin
 */
public class SafeMap<K, V> implements Map<K, V>, Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7988348164479815493L;

    /**
     * Values factory.
     */
    private final IMapValueFactory<K, V> m_factory;

    /**
     * Real map.
     */
    private final Map<K, V> m_map;

    /**
     * Constructor.
     */
    public SafeMap() {
        this(null, null);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     */
    public SafeMap(Map<K, V> map) {
        this(map, null);
    }

    /**
     * Constructor.
     * 
     * @param factory default values factory
     */
    public SafeMap(IMapValueFactory<K, V> factory) {
        this(null, factory);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     * @param factory default values factory
     */
    public SafeMap(Map<K, V> map, IMapValueFactory<K, V> factory) {
        m_map = map != null ? map : new HashMap<K, V>();
        m_factory = DefaultMapValueFactory.wrapFactory(factory);
    }

    /**
     * Returns the value to which the specified key is mapped.
     * 
     * @param key key for searching
     * @return an instance of the <code>V</code> type
     * @see java.util.TreeMap#get(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public V get(final Object key) {
        final K kkey = (K) key;
        V value = m_map.get(key);
        if (value == null) {
            value = m_factory.create(kkey);
            if (value != null) {
                m_map.put(kkey, value);
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#clear()
     */
    public void clear() {
        m_map.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        return m_map.containsKey(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        return m_map.containsValue(value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return m_map.entrySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return m_map.equals(o);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#hashCode()
     */
    public int hashCode() {
        return m_map.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return m_map.isEmpty();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return m_map.keySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        return m_map.put(key, value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> t) {
        m_map.putAll(t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key) {
        return m_map.remove(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#size()
     */
    public int size() {
        return m_map.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return m_map.values();
    }

    /**
     * @return the real map
     */
    Map<K, V> getRealMap() {
        return m_map;
    }
}
