package org.ak2.utils.collections;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedSortedMap<K, V> extends GuardedMap<K, V> implements SortedMap<K, V> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5392000041435878081L;

    /**
     * Real map
     */
    private SortedMap<K, V> m_map;

    /**
     * Constructor.
     */
    public GuardedSortedMap() {
        this(new TreeMap<K, V>(), null);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     */
    public GuardedSortedMap(final SortedMap<K, V> map) {
        this(map, null);
    }

    /**
     * Constructor.
     * 
     * @param c comparator to sort map
     */
    public GuardedSortedMap(final Comparator<? super K> c) {
        this(new TreeMap<K, V>(c), null);
    }

    /**
     * Constructor.
     * 
     * @param lock guard lock
     */
    public GuardedSortedMap(final ReentrantReadWriteLock lock) {
        this(new TreeMap<K, V>(), lock);
    }

    /**
     * Constructor.
     * 
     * @param c comparator to sort map
     * @param lock guard lock
     */
    public GuardedSortedMap(final Comparator<? super K> c, final ReentrantReadWriteLock lock) {
        this(new TreeMap<K, V>(c), lock);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     * @param lock guard lock
     */
    public GuardedSortedMap(final SortedMap<K, V> map, final ReentrantReadWriteLock lock) {
        super(map != null ? map : new TreeMap<K, V>(), lock);
        m_map = getRealCollection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#comparator()
     */
    public Comparator<? super K> comparator() {
        return m_map.comparator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#firstKey()
     */
    public K firstKey() {
        lockRead();
        try {
            return m_map.firstKey();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#headMap(java.lang.Object)
     */
    public SortedMap<K, V> headMap(final K toKey) {
        lockRead();
        try {
            return m_map.headMap(toKey);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#lastKey()
     */
    public K lastKey() {
        lockRead();
        try {
            return m_map.lastKey();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
     */
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        lockRead();
        try {
            return m_map.subMap(fromKey, toKey);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#tailMap(java.lang.Object)
     */
    public SortedMap<K, V> tailMap(final K fromKey) {
        lockRead();
        try {
            return m_map.tailMap(fromKey);
        } finally {
            unlockRead();
        }
    }

}
