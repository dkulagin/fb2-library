package org.ak2.gui.controls.table.policies;

import org.ak2.gui.controls.table.TableEx;

/**
 * This interface defines methods for defining preferred column width
 */
public interface ITableColumnsResizePolicy {
    /**
     * Sets preferred column width for the given table
     * 
     * @param table
     *            the table
     */
    public void resizeColumns(TableEx table);
}
