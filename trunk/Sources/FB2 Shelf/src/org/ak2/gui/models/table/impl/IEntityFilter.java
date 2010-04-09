package org.ak2.gui.models.table.impl;

/**
 * This interface defines filter for table entities
 * 
 * @param <Entity>
 *            entity type
 * 
 * @author Alexander Kasatkin
 */
public interface IEntityFilter<Entity> {
    /**
     * Checks the given entity
     * 
     * @param entity
     *            entity to accept
     * @return true or false
     */
    public boolean accept(Entity entity);
}
