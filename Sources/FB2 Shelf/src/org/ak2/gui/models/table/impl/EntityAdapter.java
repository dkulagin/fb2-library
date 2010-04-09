package org.ak2.gui.models.table.impl;

import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;

/**
 * Represent the original entity for the given column
 */
public class EntityAdapter implements ITableColumnAdapter {
    private Class<?> fieldClass;

    /**
     * Constructor
     * 
     * @param entityClass
     *            the entity class
     */
    public EntityAdapter(final Class<?> entityClass) {
        fieldClass = entityClass;
    }

    /**
     * Returns the class of cell value.
     * 
     * @return a <code>Class</code> object.
     * @see ITableColumnAdapter#getCellClass()
     */
    public Class<?> getCellClass() {
        return fieldClass;
    }

    /**
     * Retrieves cell value from the given entity.
     * 
     * @param model
     *            the source model
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     * @param entity
     *            the entity
     * @return a <code>Object</code> object.
     * @see ITableColumnAdapter#getCellValue(ITableModel, int, int, java.lang.Object)
     */
    public Object getCellValue(final ITableModel<?, ?> model, final int rowIndex, final int columnIndex, final Object entity) {
        return entity;
    }

}
