package org.ak2.gui.controls.table.policies;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.ak2.gui.controls.table.TableEx;

/**
 * This class sets the preferred size of columns to a maximum size of its content
 */
public class ContentResizePolicy implements ITableColumnsResizePolicy {
    private static final int CELL_INSET = 4;

    /**
     * Sets preferred column width for the given table
     * 
     * @param table
     *            the table
     * @see ITableColumnsResizePolicy#resizeColumns(TableEx)
     */
    public void resizeColumns(final TableEx table) {
        int viewportWidth = table.getWidth();
        JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, table);
        if (viewport != null) {
            viewportWidth = viewport.getWidth();
        }

        int columnCount = table.getColumnCount();
        int[] columnWidths = new int[columnCount];
        int[] columnRealWidths = new int[columnCount];

        int totalWidth = 0;
        int totalRealWidth = 0;
        for (int i = 0; i < columnCount; i++) {
            int tableColumnWidth = getTableColumnWidth(table, i);
            int tableHeaderWidth = getTableHeaderWidth(table, i);
            columnWidths[i] = Math.max(tableColumnWidth, tableHeaderWidth);
            columnRealWidths[i] = table.getColumnModel().getColumn(i).getWidth();
            totalWidth += columnWidths[i];
        }

        int maxWidth = Math.max(totalWidth, totalRealWidth);

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            setTableColumnWidth(table, columnIndex, columnWidths[columnIndex]);
        }

        table.setAutoResizeMode(maxWidth < viewportWidth ? JTable.AUTO_RESIZE_ALL_COLUMNS : JTable.AUTO_RESIZE_OFF);
    }

    /**
     * Gets width of the column header with the given index.
     * 
     * @param table
     *            the table.
     * @param columnIndex
     *            the column index.
     * @return column header width.
     */
    private int getTableHeaderWidth(final TableEx table, final int columnIndex) {
        int res = 0;
        TableColumn aColumn = table.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        if (renderer != null) {
            Component c = renderer.getTableCellRendererComponent(table, aColumn.getHeaderValue(), false, false, -1, columnIndex);
            res = CELL_INSET + c.getPreferredSize().width;
        }
        return res;
    }

    /**
     * Gets width of the column with the given index.
     * 
     * @param table
     *            the table.
     * @param columnIndex
     *            the column index.
     * @return column width.
     */
    private static int getTableColumnWidth(final TableEx table, final int columnIndex) {
        int res = 0;
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        int realColumnIndex = column.getModelIndex();
        for (int rowIndex = 0; rowIndex < table.getModel().getRowCount(); rowIndex++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, realColumnIndex);
            if (cellRenderer != null) {
                Object value = table.getModel().getValueAt(rowIndex, realColumnIndex);
                Component cc = cellRenderer.getTableCellRendererComponent(table, value, true, true, rowIndex, realColumnIndex);
                res = Math.max(res, CELL_INSET + cc.getPreferredSize().width);
            }
        }
        return res;
    }

    /**
     * Sets the width of the column with the given index.
     * 
     * @param table
     *            the table.
     * @param columnIndex
     *            column index.
     * @param prefWidth
     *            preferred width.
     */
    private static void setTableColumnWidth(final TableEx table, final int columnIndex, final int prefWidth) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        if (prefWidth > 0 && column.getPreferredWidth() != prefWidth) {
            column.setPreferredWidth(prefWidth);
            if (column.getMinWidth() > prefWidth) {
                column.setMinWidth(prefWidth);
            }
            if (column.getMaxWidth() < prefWidth) {
                column.setMaxWidth(prefWidth);
            }
        }
    }
}
