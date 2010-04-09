package org.ak2.gui.models.table.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ak2.gui.models.table.IStorage;
import org.ak2.utils.LengthUtils;

/**
 * @param <Entity>
 *            entity class
 * 
 * @author Alexander Kasatkin
 */
public class ListStorage<Entity> implements IStorage<Entity> {
    private List<Entity> fieldEntities = null;

    /**
     * Constructor.
     * 
     * @param list
     *            the entity list
     */
    public ListStorage(final List<Entity> list) {
        fieldEntities = list != null ? list : new ArrayList<Entity>();
    }

    /**
     * Adds entity to the given row
     * 
     * @param row
     *            row index
     * @param entity
     *            entity to add
     * @return index of added entity
     * @see IStorage#addEntity(int, java.lang.Object)
     */
    public int addEntity(final int row, final Entity entity) {
        fieldEntities.add(row, entity);
        return row;
    }

    /**
     * Remove all entities from the model
     * 
     * @see IStorage#clear()
     */
    public void clear() {
        fieldEntities.clear();
    }

    /**
     * Gets an entity for the given index.
     * 
     * @param rowIndex
     *            the entity index
     * @return corresponding entity or null.
     * @see IStorage#getEntity(int)
     */
    public Entity getEntity(final int rowIndex) {
        return fieldEntities.get(rowIndex);
    }

    /**
     * Gets the entities.
     * 
     * @return the entities
     * @see IStorage#getEntities()
     */
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(fieldEntities);
    }

    /**
     * Returns the number of rows in the model.
     * 
     * @return number of rows
     * @see IStorage#getEntityCount()
     */
    public int getEntityCount() {
        return fieldEntities.size();
    }

    /**
     * Returns the index of row contained the given entity
     * 
     * @param entity
     *            entity to find
     * @return index or -1
     * @see IStorage#getRowOf(java.lang.Object)
     */
    public int getRowOf(final Entity entity) {
        return fieldEntities.indexOf(entity);
    }

    /**
     * Remove entity from the model
     * 
     * @param row
     *            row to delete
     * @see IStorage#removeEntity(int)
     */
    public void removeEntity(final int row) {
        fieldEntities.remove(row);
    }

    /**
     * Sets an array of entities.
     * 
     * @param list
     *            an array of entities.
     * @see IStorage#setEntities(java.lang.Object[])
     */
    public void setEntities(final Entity[] list) {
        fieldEntities.clear();
        if (LengthUtils.isNotEmpty(list)) {
            Collections.addAll(fieldEntities, list);
        }
    }

    /**
     * Set entity instead of existing
     * 
     * @param row
     *            row to change
     * @param entity
     *            new entity
     * @see IStorage#setEntity(int, java.lang.Object)
     */
    public void setEntity(final int row, final Entity entity) {
        fieldEntities.set(row, entity);
    }

    /**
     * Swaps two entities
     * 
     * @param row1
     *            index of first entity
     * @param row2
     *            index of second entity
     * @see IStorage#swap(int, int)
     */
    public void swap(final int row1, final int row2) {
        int entityCount = getEntityCount();
        boolean valid1 = row1 >= 0 && row1 < entityCount;
        boolean valid2 = row2 >= 0 && row2 < entityCount;

        if (valid1 && valid2) {
            Entity entity1 = getEntity(row1);
            Entity entity2 = getEntity(row2);

            setEntity(row1, entity2);
            setEntity(row2, entity1);
        }
    }

}
