package org.ak2.utils.collections;

import java.util.Comparator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.ak2.utils.collections.factories.IntegerValueFactory;

/**
 * @author Alexander Kasatkin
 */
public class CountersMap<K> extends GuardedSortedMap<K, Integer> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2115674025588942230L;

    /**
     * Constructor.
     */
    public CountersMap() {
        super(new SafeSortedMap<K, Integer>(new IntegerValueFactory<K>()));
    }

    /**
     * Constructor.
     * 
     * @param c key comparator
     */
    public CountersMap(Comparator<? super K> c) {
        super(new SafeSortedMap<K, Integer>(c, new IntegerValueFactory<K>()));
    }

    /**
     * Constructor.
     * 
     * @param lock map lock
     */
    public CountersMap(ReentrantReadWriteLock lock) {
        super(new SafeSortedMap<K, Integer>(new IntegerValueFactory<K>()), lock);
    }

    /**
     * Constructor.
     * 
     * @param c key comparator
     * @param lock map lock
     */
    public CountersMap(Comparator<? super K> c, ReentrantReadWriteLock lock) {
        super(new SafeSortedMap<K, Integer>(c, new IntegerValueFactory<K>()), lock);
    }

    /**
     * Increments the corresponding counter
     * 
     * @param key counter key
     */
    public void increment(K key) {
        lockWrite();
        try {
            final int oldValue = get(key);
            final int newValue = oldValue + 1;
            put(key, newValue);
        } finally {
            unlockWrite();
        }
    }

    /**
     * Increments the corresponding counter and returns the new value.
     * 
     * @param key counter key
     * @return new counter value
     */
    public int incrementAndGet(K key) {
        lockWrite();
        try {
            final int oldValue = get(key);
            final int newValue = oldValue + 1;
            put(key, newValue);
            return newValue;
        } finally {
            unlockWrite();
        }
    }

    /**
     * Increments the corresponding counter and returns the old value.
     * 
     * @param key counter key
     * @return old counter value
     */
    public int getAndIncrement(K key) {
        lockWrite();
        try {
            final int oldValue = get(key);
            final int newValue = oldValue + 1;
            put(key, newValue);
            return oldValue;
        } finally {
            unlockWrite();
        }
    }

}
