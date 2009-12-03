package org.ak2.utils.collections;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 */
public class GuardedSet<E> extends GuardedCollection<E> implements Set<E> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4386333598970047278L;

    /**
     * Constructor.
     */
    public GuardedSet() {
        this(null, null);
    }

    /**
     * Constructor.
     * 
     * @param set real set
     */
    public GuardedSet(final Set<E> set) {
        this(set, null);
    }

    /**
     * Constructor.
     * 
     * @param lock guard lock
     */
    public GuardedSet(final ReentrantReadWriteLock lock) {
        this(null, lock);
    }

    /**
     * Constructor.
     * 
     * @param set real set
     * @param lock guard lock
     */
    public GuardedSet(final Set<E> set, final ReentrantReadWriteLock lock) {
        super(set != null ? set : new HashSet<E>(), lock);
    }

}
