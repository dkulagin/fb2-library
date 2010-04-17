/**
 *
 */
package org.ak2.utils.refs;

import java.lang.ref.WeakReference;

/**
 * @author Whippet
 * 
 */
public final class ReferenceUtils {
    /**
     * Constructor
     */
    private ReferenceUtils() {
    }

    /**
     * Creates a weak reference for the given entity.
     * 
     * @param <Entity>
     *            entity type
     * @param entity
     *            the entity
     * 
     * @return the weak reference<entity>
     */
    public static <Entity> WeakReference<Entity> create(final Entity entity) {
        return entity != null ? new WeakReference<Entity>(entity) : null;
    }

    /**
     * Gets an entity from the given reference.
     * 
     * @param <Entity>
     *            entity type
     * @param ref
     *            the weak reference
     * 
     * @return the entity
     */
    public static <Entity> Entity get(final WeakReference<Entity> ref) {
        return ref != null ? ref.get() : null;
    }

    /**
     * Gets an entity from the given reference.
     * 
     * @param <Entity>
     *            entity type
     * @param ref
     *            the reference
     * @exception Exception
     *                exceptions
     * @return the entity
     */
    public static <Entity> Entity get(final IRef<Entity> ref) throws Exception {
        return ref != null ? ref.get() : null;
    }

    /**
     * Sets an entity to the given reference.
     * 
     * @param <Entity>
     *            entity type
     * @param ref
     *            the reference
     * @param entity
     *            the entity to hold
     * @return a reference
     */
    public static <Entity> IRef<Entity> set(final IRef<Entity> ref, final Entity entity) {
        if (ref == null) {
            return new DirectRef<Entity>(entity);
        }
        ref.set(entity);
        return ref;
    }

    /**
     * Creates direct reference.
     * 
     * @param <Entity>
     *            entity type
     * 
     * @param ref
     *            original reference
     * @return an instance of the {@link DirectRef} object
     * @throws Exception
     *             thrown by the original reference
     */
    public static <Entity> IRef<Entity> direct(final IRef<Entity> ref) throws Exception {
        if (ref instanceof DirectRef<?>) {
            return ref;
        }
        return new DirectRef<Entity>(get(ref));
    }

    /**
     * Creates weak reference.
     * 
     * @param <Entity>
     *            entity type
     * 
     * @param ref
     *            original reference
     * @return an instance of the {@link WeakRef} object
     * @throws Exception
     *             thrown by the original reference
     */
    public static <Entity> IRef<Entity> weak(final IRef<Entity> ref) throws Exception {
        if (ref instanceof WeakRef<?>) {
            return ref;
        }
        return new WeakRef<Entity>(get(ref));
    }

}
