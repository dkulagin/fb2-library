package org.ak2.utils.collections;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedSortedSet<E> extends GuardedSet<E> implements SortedSet<E> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -6232418512167117756L;

    /**
     * Real set.
     */
    private final SortedSet<E> m_set;

    /**
     * Constructor.
     */
    public GuardedSortedSet() {
        this(new TreeSet<E>(), null);
    }

    /**
     * Constructor.
     * 
     * @param c comparator to sort set
     */
    public GuardedSortedSet(final Comparator<? super E> c) {
        this(new TreeSet<E>(c), null);
    }

    /**
     * Constructor.
     * 
     * @param set real set
     */
    public GuardedSortedSet(final SortedSet<E> set) {
        this(set, null);
    }

    /**
     * Constructor.
     * 
     * @param lock guard lock
     */
    public GuardedSortedSet(final ReentrantReadWriteLock lock) {
        this(new TreeSet<E>(), lock);
    }

    /**
     * Constructor.
     * 
     * @param c comparator to sort set
     * @param lock guard lock
     */
    public GuardedSortedSet(final Comparator<? super E> c, final ReentrantReadWriteLock lock) {
        this(new TreeSet<E>(c), lock);
    }

    /**
     * Constructor.
     * 
     * @param set real set
     * @param lock guard lock
     */
    public GuardedSortedSet(final SortedSet<E> set, final ReentrantReadWriteLock lock) {
        super(set != null ? set : new TreeSet<E>(), lock);
        m_set = getRealCollection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedSet#comparator()
     */
    public Comparator<? super E> comparator() {
        return m_set.comparator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedSet#first()
     */
    public E first() {
        lockRead();
        try {
            return m_set.first();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedSet#headSet(java.lang.Object)
     */
    public SortedSet<E> headSet(final E toElement) {
        lockRead();
        try {
            return m_set.headSet(toElement);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedSet#last()
     */
    public E last() {
        lockRead();
        try {
            return m_set.last();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
     */
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        lockRead();
        try {
            return m_set.subSet(fromElement, toElement);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.SortedSet#tailSet(java.lang.Object)
     */
    public SortedSet<E> tailSet(final E fromElement) {
        lockRead();
        try {
            return m_set.tailSet(fromElement);
        } finally {
            unlockRead();
        }
    }

}
