package org.ak2.gui.models.table;

/**
 * @param <Entity>
 *            entity class
 * @param <EntityContainer>
 *            entity container class
 * 
 * @author Alexander Kasatkin
 */
public interface IFactory<Entity, EntityContainer> {
    /**
     * @return new entity
     */
    public Entity newInstance();

    /**
     * Creates new storage for the given container
     * 
     * @param container
     *            container to wrap
     * @return an instance of the {@link IStorage} interface
     */
    IStorage<Entity> newStorage(final EntityContainer container);
}
