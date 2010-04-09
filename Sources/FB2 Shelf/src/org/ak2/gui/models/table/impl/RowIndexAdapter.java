package org.ak2.gui.models.table.impl;

import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;

/**
 * @author Alexander Kasatkin
 */
public class RowIndexAdapter implements ITableColumnAdapter {
    private int fieldStartIndex;

    /**
     * Constructor.
     * 
     * @param startIndex
     *            initial value for the "Row Index" column
     */
    public RowIndexAdapter(final int startIndex) {
        fieldStartIndex = startIndex;
    }

    /**
     * Returns the class of cell value.
     * 
     * @return a <code>Integer.class</code> object.
     * @see ITableColumnAdapter#getCellClass()
     */
    public Class<?> getCellClass() {
        return Integer.class;
    }

    /**
     * Retrieves row index value for the given entity.
     * 
     * @param model
     *            the source model
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     * @param entity
     *            the entity
     * @return a <code>Integer</code> object.
     * @see ITableColumnAdapter#getCellValue(ITableModel, int, int, java.lang.Object)
     */
    public Object getCellValue(final ITableModel<?, ?> model, final int rowIndex, final int columnIndex, final Object entity) {
        return new Integer(fieldStartIndex + rowIndex);
    }

    /**
     * @return value of StartIndex field
     */
    public int getStartIndex() {
        return fieldStartIndex;
    }

    /**
     * Sets value of StartIndex field
     * 
     * @param startIndex
     *            the new value
     */
    public void setStartIndex(final int startIndex) {
        fieldStartIndex = startIndex;
    }

}
