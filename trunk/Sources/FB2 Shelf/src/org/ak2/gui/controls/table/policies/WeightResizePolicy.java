package org.ak2.gui.controls.table.policies;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.ak2.gui.controls.table.TableEx;
import org.ak2.utils.LengthUtils;

/**
 * This class defines preferred column width using wieghts of columns
 */
public class WeightResizePolicy implements ITableColumnsResizePolicy {
    private final int fieldTotalWeight;
    private int[] fieldWeights;

    /**
     * Constructor
     * 
     * @param weights
     *            weights of columns.
     */
    public WeightResizePolicy(final int... weights) {
        fieldWeights = weights;
        int weightSum = 0;
        for (int i = 0; i < LengthUtils.length(weights); i++) {
            weightSum += weights[i];
        }
        fieldTotalWeight = weightSum == 0 ? 1 : weightSum;
    }

    /**
     * Sets preferred column width for the given table
     * 
     * @param table
     *            the table
     * @see ITableColumnsResizePolicy#resizeColumns(TableEx)
     */
    public void resizeColumns(final TableEx table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final int columnCount = table.getColumnCount();
        final int weightCount = LengthUtils.length(fieldWeights);
        final int count = Math.min(columnCount, weightCount);

        final int tableWidth = table.getWidth();

        for (int columnIndex = 0; columnIndex < count; columnIndex++) {
            setTableColumnWidth(table, columnIndex, (tableWidth * fieldWeights[columnIndex]) / fieldTotalWeight);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
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
    protected void setTableColumnWidth(final TableEx table, final int columnIndex, final int prefWidth) {
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        if (prefWidth > 0) {
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
