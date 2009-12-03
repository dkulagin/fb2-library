package org.ak2.utils.collections;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedIterator<E> extends AbstractGuardedObject implements Iterator<E> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -1139595586722700012L;

    /**
     * Real collection.
     */
    private final Iterator<E> m_iterator;

    /**
     * Constructor.
     * 
     * @param iterator real iterator
     * @param lock guard lock
     */
    public GuardedIterator(Iterator<E> iterator, ReentrantReadWriteLock lock) {
        super(lock);
        m_iterator = iterator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        lockRead();
        try {
            return m_iterator.hasNext();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    public E next() {
        lockRead();
        try {
            return m_iterator.next();
        } finally {
            unlockRead();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        lockWrite();
        try {
            m_iterator.remove();
        } finally {
            unlockWrite();
        }
    }
}