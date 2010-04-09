package org.ak2.utils.refs;

/**
 * @param <Entity> type of holding entity.
 *
 * @author Alexander Kasatkin
 */
public interface IRef<Entity>
{
    /**
     * @return holding entity
     * @throws Exception exception
     */
    public Entity get() throws Exception;

    /**
     * @param entity new entity to hold
     */
    public void set(Entity entity);

    /**
     * Store entity and release this reference.
     *
     * @return an instance of storage reference
     */
    public IRef<Entity> store();

    /**
     * Release the reference.
     */
    public void release();
}
