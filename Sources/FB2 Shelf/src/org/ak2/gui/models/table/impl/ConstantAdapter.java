package org.ak2.gui.models.table.impl;

import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;

/**
 * Represent constant value for each row
 */
public class ConstantAdapter implements ITableColumnAdapter {
    private Object fieldConstant;

    private Class<?> fieldClass;

    /**
     * Constructor
     * 
     * @param constant
     *            the value
     */
    public ConstantAdapter(final Object constant) {
        if (constant != null) {
            fieldConstant = constant;
            fieldClass = constant.getClass();
        } else {
            fieldConstant = "";
            fieldClass = String.class;
        }
    }

    /**
     * Constructor
     * 
     * @param cellClass
     *            the value class
     * @param constant
     *            the value
     */
    public ConstantAdapter(final Class<?> cellClass, final Object constant) {
        fieldConstant = constant;
        fieldClass = cellClass;
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
        return fieldConstant;
    }

}
