/**
 *
 */
package org.ak2.utils.refs;

import java.lang.ref.WeakReference;

/**
 * @param <Entity> type of holding entity.
 *
 * @author Alexander Kasatkin
 */
public class WeakRef<Entity> implements IRef<Entity>
{
    private WeakReference<Entity> m_entity;

    /**
     * Constructor.
     *
     * @param entity the entity
     */
    public WeakRef(final Entity entity)
    {
        set(entity);
    }

    /**
     * @return holding entity
     *
     * @see com.tsystems.wmstk.commonlib.cache.IRef#get()
     */
    public synchronized Entity get()
    {
        return ReferenceUtils.get(m_entity);
    }

    /**
     * @param entity new entity to hold
     */
    public synchronized void set(final Entity entity)
    {
        m_entity = ReferenceUtils.create(entity);
    }

    /**
     * Store entity and release this reference.
     *
     * @return an instance of storage reference
     * @see com.tsystems.wmstk.commonlib.cache.IRef#store()
     */
    public IRef<Entity> store()
    {
        return this;
    }

    /**
     *
     * @see com.tsystems.wmstk.commonlib.cache.IRef#release()
     */
    public synchronized void release()
    {
        m_entity = null;
    }
}
