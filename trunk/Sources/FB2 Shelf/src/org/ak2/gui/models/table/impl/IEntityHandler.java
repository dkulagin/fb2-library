package org.ak2.gui.models.table.impl;

/**
 * This interface defines handler for table entities
 * 
 * @param <Entity>
 *            entity type
 * 
 * @author Alexander Kasatkin
 */
public interface IEntityHandler<Entity> extends IEntityFilter<Entity> {
    /**
     * Handle the given entity
     * 
     * @param rowIndex
     *            index of entity row
     * @param entity
     *            entity to handle
     * @return true if the corresponding table row should be updated
     */
    public boolean handle(int rowIndex, Entity entity);
}
