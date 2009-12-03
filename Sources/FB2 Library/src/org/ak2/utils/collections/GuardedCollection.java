package org.ak2.utils.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedCollection<E> extends GuardedIterable<E> implements Collection<E>, Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3301203670949547561L;

    /**
     * Real collection.
     */
    private final Collection<E> m_collection;

    /**
     * Constructor.
     * 
     * @param collection the collection to wrap.
     */
    public GuardedCollection(Collection<E> collection) {
        this(collection, null);
    }

    /**
     * Constructor.
     * 
     * @param collection the collection to wrap.
     * @param lock guard lock
     */
    public GuardedCollection(Collection<E> collection, ReentrantReadWriteLock lock) {
        super(collection, lock);
        m_collection = collection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(E o) {
        lockWrite();
        try {
            return m_collection.add(o);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends E> c) {
        lockWrite();
        try {
            return m_collection.addAll(c);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * 
     * @see java.util.Collection#clear()
     */
    public void clear() {
        lockWrite();
        try {
            m_collection.clear();
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        lockRead();
        try {
            return m_collection.contains(o);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        lockRead();
        try {
            return m_collection.containsAll(c);
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return m_collection.equals(o);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#hashCode()
     */
    public int hashCode() {
        return m_collection.hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        lockRead();
        try {
            return m_collection.isEmpty();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        lockWrite();
        try {
            return m_collection.remove(o);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        lockWrite();
        try {
            return m_collection.removeAll(c);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        lockWrite();
        try {
            return m_collection.retainAll(c);
        } finally {
            unlockWrite();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#size()
     */
    public int size() {
        lockRead();
        try {
            return m_collection.size();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        lockRead();
        try {
            return m_collection.toArray();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Collection#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        lockRead();
        try {
            return m_collection.toArray(a);
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
            return m_collection.toString();
        } finally {
            unlockRead();
        }
    }
}
