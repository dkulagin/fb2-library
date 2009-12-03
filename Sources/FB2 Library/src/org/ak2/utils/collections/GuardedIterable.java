package org.ak2.utils.collections;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedIterable<E> extends AbstractGuardedObject implements Iterable<E> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4086091433964452412L;

    /**
     * Original collection.
     */
    private Iterable<E> m_original;

    /**
     * Constructor.
     * 
     * @param iterable the collection to wrap.
     */
    public GuardedIterable(Iterable<E> iterable) {
        this(iterable, null);
    }

    /**
     * Constructor.
     * 
     * @param iterable the collection to wrap.
     * @param lock guard lock
     */
    public GuardedIterable(Iterable<E> iterable, ReentrantReadWriteLock lock) {
        super(lock);
        m_original = iterable;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Iterable#iterator()
     */
    public final Iterator<E> iterator() {
        return new GuardedIterator<E>(m_original.iterator(), getLock());
    }

    /**
     * @param <T> collection type
     * @return real collection
     */
    @SuppressWarnings("unchecked")
    final <T extends Iterable<E>> T getRealCollection() {
        return (T) m_original;
    }
}
