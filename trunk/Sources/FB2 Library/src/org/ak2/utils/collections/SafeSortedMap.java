package org.ak2.utils.collections;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ak2.utils.collections.factories.IMapValueFactory;

/**
 * @author Alexander Kasatkin
 */
public class SafeSortedMap<K, V> extends SafeMap<K, V> implements SortedMap<K, V> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -611892990111088635L;

    /**
     * Real map.
     */
    private final SortedMap<K, V> m_map;

    /**
     * Constructor.
     */
    public SafeSortedMap() {
        this(new TreeMap<K, V>(), null);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     */
    public SafeSortedMap(final SortedMap<K, V> map) {
        this(map, null);
    }

    /**
     * Constructor.
     * 
     * @param c the comparator to sort internal map
     */
    public SafeSortedMap(final Comparator<? super K> c) {
        this(new TreeMap<K, V>(c), null);
    }

    /**
     * Constructor.
     * 
     * @param factory default values factory
     */
    public SafeSortedMap(final IMapValueFactory<K, V> factory) {
        this(new TreeMap<K, V>(), factory);
    }

    /**
     * Constructor.
     * 
     * @param c the comparator to sort internal map
     * @param factory default values factory
     */
    public SafeSortedMap(final Comparator<? super K> c, final IMapValueFactory<K, V> factory) {
        this(new TreeMap<K, V>(c), factory);
    }

    /**
     * Constructor.
     * 
     * @param map the map whose mappings are to be placed in this map.
     * @param factory default values factory
     */
    public SafeSortedMap(final SortedMap<K, V> map, final IMapValueFactory<K, V> factory) {
        super(map != null ? map : new TreeMap<K, V>(), factory);
        m_map = (SortedMap<K, V>) getRealMap();
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
        return m_map.firstKey();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#headMap(java.lang.Object)
     */
    public SortedMap<K, V> headMap(final K toKey) {
        return m_map.headMap(toKey);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#lastKey()
     */
    public K lastKey() {
        return m_map.lastKey();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
     */
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return m_map.subMap(fromKey, toKey);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedMap#tailMap(java.lang.Object)
     */
    public SortedMap<K, V> tailMap(final K fromKey) {
        return m_map.tailMap(fromKey);
    }

}
