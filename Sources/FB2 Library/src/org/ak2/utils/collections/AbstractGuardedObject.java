package org.ak2.utils.collections;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alexander Kasatkin
 * 
 */
public class AbstractGuardedObject implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -7091690251931204106L;

    /**
     * Object lock.
     */
    private final ReentrantReadWriteLock m_lock;

    /**
     * Constructor.
     */
    public AbstractGuardedObject() {
        this(null);
    }

    /**
     * Constructor.
     * 
     * @param lock guard lock
     */
    public AbstractGuardedObject(final ReentrantReadWriteLock lock) {
        m_lock = lock != null ? lock : new ReentrantReadWriteLock();
    }

    /**
     * @return the guard lock
     */
    protected final ReentrantReadWriteLock getLock() {
        return m_lock;
    }

    /**
     * Acquires the read lock.
     */
    protected final void lockRead() {
        m_lock.readLock().lock();
    }

    /**
     * Attempts to release read lock.
     */
    protected final void unlockRead() {
        m_lock.readLock().unlock();
    }

    /**
     * Acquire the write lock.
     */
    protected final void lockWrite() {
        m_lock.writeLock().lock();
    }

    /**
     * Attempts to release write lock.
     */
    protected final void unlockWrite() {
        m_lock.writeLock().unlock();
    }

    /**
     * Acquires the given lock.
     * 
     * @param writeLock write lock flag
     */
    protected final void lock(boolean writeLock) {
        Lock lock = writeLock ? m_lock.writeLock() : m_lock.readLock();
        lock.lock();
    }

    /**
     * Attempts to release the given lock.
     * 
     * @param writeLock write lock flag
     */
    protected final void unlock(boolean writeLock) {
        Lock lock = writeLock ? m_lock.writeLock() : m_lock.readLock();
        lock.unlock();
    }

}
