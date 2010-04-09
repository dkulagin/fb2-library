package org.ak2.gui.controls.table.policies;

import java.awt.Dimension;

import javax.swing.table.TableColumn;

import org.ak2.gui.controls.table.TableEx;
import org.ak2.utils.LengthUtils;

/**
 * This class defines preferred column width using predefined width value and weight of column
 */
public class MixedResizePolicy implements ITableColumnsResizePolicy {
    private int[] fieldWidths;
    private double[] fieldWeights;

    /**
     * Constructor
     * 
     * @param widths
     *            an array of column widths
     */
    public MixedResizePolicy(final int[] widths) {
        fieldWidths = widths;
        fieldWeights = new double[LengthUtils.length(widths)];
        for (int i = 0; i < fieldWeights.length; i++) {
            fieldWeights[i] = 0;
        }
    }

    /**
     * Constructor
     * 
     * @param widths
     *            an array of column widths
     * @param resizableColumnIndex
     *            index of single resizable column
     */
    public MixedResizePolicy(final int[] widths, final int resizableColumnIndex) {
        fieldWidths = widths;
        fieldWeights = new double[LengthUtils.length(widths)];
        for (int i = 0; i < fieldWeights.length; i++) {
            fieldWeights[i] = i == resizableColumnIndex ? 1 : 0;
        }
    }

    /**
     * Constructor
     * 
     * @param widths
     *            an array of column widths
     * @param weights
     *            an array of colum resize weights
     */
    public MixedResizePolicy(final int[] widths, final double[] weights) {
        fieldWidths = widths;
        fieldWeights = weights;
    }

    /**
     * Sets preferred column width for the given table
     * 
     * @param table
     *            the table
     * @see ITableColumnsResizePolicy#resizeColumns(TableEx)
     */
    public void resizeColumns(final TableEx table) {
        Dimension size = table.getSize();

        int minTableWidth = 0;
        int widthCount = LengthUtils.length(fieldWidths);
        for (int i = 0; i < widthCount; i++) {
            minTableWidth += fieldWidths[i];
        }

        double scalableWeigthAmount = 0;
        int weightCount = LengthUtils.length(fieldWeights);
        for (int i = 0; i < weightCount; i++) {
            if (fieldWeights[i] > 0) {
                scalableWeigthAmount += fieldWeights[i];
            }
        }
        if (scalableWeigthAmount == 0) {
            scalableWeigthAmount = 1;
        }

        final int freeHorzSpace = (minTableWidth < size.width) ? (size.width - minTableWidth) : (0);

        for (int i = 0; i < widthCount; i++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(i);

            int preferredWidth = fieldWidths[i];
            if (i < weightCount && fieldWeights[i] > 0) {
                preferredWidth += (int) ((freeHorzSpace / scalableWeigthAmount) * fieldWeights[i]);
            }
            tableColumn.setPreferredWidth(preferredWidth);
            tableColumn.setMinWidth(preferredWidth);
            tableColumn.setMaxWidth(preferredWidth);
        }
    }
}
