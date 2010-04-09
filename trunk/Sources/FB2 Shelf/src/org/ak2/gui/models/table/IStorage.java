package org.ak2.gui.models.table;

import java.util.List;

/**
 * @param <Entity>
 *            entity class
 * 
 * @author Alexander Kasatkin
 */
public interface IStorage<Entity> {
    /**
     * Adds entity to the given row
     * 
     * @param row
     *            row index
     * @param entity
     *            entity to add
     * @return index of added entity
     */
    public int addEntity(final int row, final Entity entity);

    /**
     * Remove all entities from the storage
     */
    public void clear();

    /**
     * Gets an entity for the given index.
     * 
     * @param rowIndex
     *            the entity index
     * @return corresponding entity or null.
     */
    public Entity getEntity(final int rowIndex);

    /**
     * Gets the entities.
     * 
     * @return the entities
     */
    public List<Entity> getEntities();

    /**
     * Returns the number of entities in the storage.
     * 
     * @return number of rows
     */
    public int getEntityCount();

    /**
     * Returns the index of row contained the given entity
     * 
     * @param entity
     *            entity to find
     * @return index or -1
     */
    public int getRowOf(final Entity entity);

    /**
     * Remove entity from the storage
     * 
     * @param row
     *            row to delete
     */
    public void removeEntity(final int row);

    /**
     * Set entity instead of existing
     * 
     * @param row
     *            row to change
     * @param entity
     *            new entity
     */
    public void setEntity(final int row, final Entity entity);

    /**
     * Swaps two entities
     * 
     * @param row1
     *            index of first entity
     * @param row2
     *            index of second entity
     */
    public void swap(final int row1, final int row2);
}
