package org.ak2.gui.models.table;

/**
 * This interface defines common features for all adapters retrieving table cell data from an entity.
 */
public interface ITableColumnAdapter {

    /**
     * Returns the class of cell value.
     * 
     * @return a <code>Class</code> object.
     */
    Class<?> getCellClass();

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
     */
    Object getCellValue(ITableModel<?, ?> model, int rowIndex, int columnIndex, Object entity);
}
