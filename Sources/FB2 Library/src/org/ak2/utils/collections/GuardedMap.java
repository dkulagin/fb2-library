package org.ak2.utils.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedMap<K, V> extends AbstractGuardedObject implements Map<K, V>, Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1746600653889417980L;

    /**
     * Real map.
     */
    private final Map<K, V> m_map;

    /**
     * Safe map flag.
     */
    private final boolean m_writeGetOp;

    /**
     * Constructor.
     */
    public GuardedMap() {
        this(null, null);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     */
    public GuardedMap(final Map<K, V> map) {
        this(map, null);
    }

    /**
     * Constructor.
     * 
     * @param lock guard lock
     */
    public GuardedMap(final ReentrantReadWriteLock lock) {
        this(null, lock);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     * @param lock guard lock
     */
    @SuppressWarnings("unchecked")
    public GuardedMap(final Map<K, V> map, final ReentrantReadWriteLock lock) {
        super(lock);
        m_map = map != null ? map : new HashMap<K, V>();
        m_writeGetOp = m_map instanceof SafeMap || m_map instanceof LinkedHashMap;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#clear()
     */
    public void clear() {
        lockWrite();
        try {
            m_map.clear();
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(final Object key) {
        lockRead();
        try {
            return m_map.containsKey(key);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(final Object value) {
        lockRead();
        try {
            return m_map.containsValue(value);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new GuardedSet<Entry<K, V>>(m_map.entrySet(), getLock());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        lockRead();
        try {
            return m_map.equals(o);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(final Object key) {
        lock(m_writeGetOp);
        try {
            return m_map.get(key);
        } finally {
            unlock(m_writeGetOp);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#hashCode()
     */
    @Override
    public int hashCode() {
        return m_map.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        lockRead();
        try {
            return m_map.isEmpty();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return new GuardedSet<K>(m_map.keySet(), getLock());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(final K key, final V value) {
        lockWrite();
        try {
            return m_map.put(key, value);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(final Map<? extends K, ? extends V> t) {
        lockWrite();
        try {
            m_map.putAll(t);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(final Object key) {
        lockWrite();
        try {
            return m_map.remove(key);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#size()
     */
    public int size() {
        lockRead();
        try {
            return m_map.size();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        lockRead();
        try {
            return m_map.toString();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        return new GuardedCollection<V>(m_map.values(), getLock());
    }

    /**
     * @param <T> collection type
     * @return real collection
     */
    @SuppressWarnings("unchecked")
    <T extends Map<K, V>> T getRealCollection() {
        return (T) m_map;
    }

}
