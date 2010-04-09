package org.ak2.gui.controls.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.ak2.gui.controls.table.policies.ITableColumnsResizePolicy;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.table.SortType;
import org.ak2.gui.resources.ResourceManager;
import org.ak2.utils.LengthUtils;

/**
 * This class enhance the standard SWING table
 */
public class TableEx extends JTable {
    private static final long serialVersionUID = -7108750762504133686L;

    private static ImageIcon s_ascendIcon = ResourceManager.getInstance().getIcon("ui/table/c_asort.gif");

    private static ImageIcon s_descendIcon = ResourceManager.getInstance().getIcon("ui/table/c_dsort.gif");

    private ITableColumnsResizePolicy fieldResizePolicy;

    private String m_sortingColumn = "";

    private SortType m_sortingType = SortType.None;

    /**
     * Constructor
     */
    public TableEx() {
        super();
        getTableHeader().setUI(new SortableTableHeaderUI());
    }

    /**
     * Constructor
     * 
     * @param dm
     *            the data model
     */
    public TableEx(final TableModel dm) {
        super(dm);
        getTableHeader().setUI(new SortableTableHeaderUI());
    }

    /**
     * Sets the data model for this table to <code>newModel</code> and registers with it for listener notifications from the new data model.
     * 
     * @param model
     *            the new data source for this table
     * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
     */
    @Override
    public void setModel(final TableModel model) {
        if (model instanceof ITableModel<?, ?>) {
            ITableModel<?, ?> tableModel = ((ITableModel<?, ?>) model);
            int index = -1;
            if (LengthUtils.isNotEmpty(m_sortingColumn) && m_sortingType != SortType.None) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (m_sortingColumn.equals(tableModel.getColumnName(i))) {
                        index = i;
                        break;
                    }
                }
            }
            tableModel.sortBy(index, m_sortingType);
        } else {
            m_sortingColumn = "";
            m_sortingType = SortType.None;
        }

        super.setModel(model);
    }

    /**
     * Getter for the ResizePolicy properrty
     * 
     * @return a <code>ITableColumnsResizePolicy</code> object or <code>null</code>
     */
    public ITableColumnsResizePolicy getResizePolicy() {
        return fieldResizePolicy;
    }

    /**
     * Setter for the ResizePolicy properrty
     * 
     * @param policy
     *            new resize policy
     */
    public void setResizePolicy(final ITableColumnsResizePolicy policy) {
        fieldResizePolicy = policy;
    }

    /**
     * Moves and resizes this component. The new location of the top-left corner is specified by <code>x</code> and <code>y</code>, and the new size is
     * specified by <code>width</code> and <code>height</code>.
     * 
     * @param x
     *            the new <i>x</i>-coordinate of this component
     * @param y
     *            the new <i>y</i>-coordinate of this component
     * @param width
     *            the new <code>width</code> of this component
     * @param height
     *            the new <code>height</code> of this component
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        if (fieldResizePolicy != null) {
            fieldResizePolicy.resizeColumns(this);
        }
    }

    /**
     * Invoked when this table's <code>TableModel</code> generates a <code>TableModelEvent</code>. The <code>TableModelEvent</code> should be constructed in the
     * coordinate system of the model; the appropriate mapping to the view coordinate system is performed by this <code>JTable</code> when it receives the
     * event.
     * <p>
     * Application code will not use these methods explicitly, they are used internally by <code>JTable</code>.
     * <p>
     * Note that as of 1.3, this method clears the selection, if any.
     * 
     * @param e
     *            table event
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    @Override
    public void tableChanged(final TableModelEvent e) {
        super.tableChanged(e);
        if (fieldResizePolicy != null) {
            fieldResizePolicy.resizeColumns(this);
        }
    }

    /**
     * If the <code>preferredSize</code> has been set to a non-<code>null</code> value just returns it. If the UI delegate's <code>getPreferredSize</code>
     * method returns a non <code>null</code> value then return that; otherwise defer to the component's layout manager.
     * 
     * @return the value of the <code>preferredSize</code> property
     * @see java.awt.Component#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        if (fieldResizePolicy != null) {
            fieldResizePolicy.resizeColumns(this);
        }
        return super.getPreferredSize();
    }

    /**
     * Returns an appropriate renderer for the cell specified by this row and column.
     * 
     * @param row
     *            the row of the cell to render, where 0 is the first row
     * @param column
     *            the column of the cell to render, where 0 is the first column
     * @return the assigned renderer; if <code>null</code> returns the default renderer for this type of object
     * @see javax.swing.JTable#getCellRenderer(int, int)
     */
    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellRenderer renderer = tableColumn.getCellRenderer();
        Object value = getModel().getValueAt(row, column);
        if (value != null) {
            renderer = getDefaultRenderer(value.getClass());
        }
        if (renderer == null) {
            renderer = getDefaultRenderer(getColumnClass(column));
        }
        return renderer;
    }

    /**
     * Returns the cell renderer to be used when no renderer has been set in a <code>TableColumn</code>.
     * 
     * @param columnClass
     *            return the default cell renderer for this columnClass
     * @return the renderer for this columnClass
     * @see javax.swing.JTable#getDefaultRenderer(java.lang.Class)
     */
    @Override
    public TableCellRenderer getDefaultRenderer(final Class<?> columnClass) {
        if (columnClass == null) {
            return null;
        } else {
            Object renderer = defaultRenderersByColumnClass.get(columnClass);
            if (renderer != null) {
                return (TableCellRenderer) renderer;
            } else {
                TableCellRenderer defaultRenderer = null;
                Class<?>[] interfaces = columnClass.getInterfaces();
                for (int i = 0; i < interfaces.length && defaultRenderer == null; i++) {
                    defaultRenderer = getDefaultRenderer(interfaces[i]);
                }
                if (defaultRenderer == null) {
                    defaultRenderer = getDefaultRenderer(columnClass.getSuperclass());
                }
                return defaultRenderer;
            }
        }
    }

    /**
     * Overrides <code>JComponent</code>'s <code>getToolTipText</code> method in order to allow the model's tips to be used if it has text set.
     * 
     * @param event
     *            mouse event
     * @return string
     * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
     */
    @Override
    public String getToolTipText(final MouseEvent event) {
        final TableModel model = getModel();
        final Point point = event.getPoint();
        final int hitColumnIndex = columnAtPoint(point);
        final int hitRowIndex = rowAtPoint(point);

        String tip = null;

        if ((hitColumnIndex != -1) && (hitRowIndex != -1)) {
            if (model instanceof ITableModel) {
                tip = ((ITableModel<?, ?>) model).getTooltip(hitRowIndex);
            }
        }
        if (tip == null) {
            tip = super.getToolTipText(event);
        }
        return tip;
    }

    /**
     *
     */
    private class SortableTableHeaderUI extends BasicTableHeaderUI {
        /**
         * Constructor
         */
        public SortableTableHeaderUI() {
        }

        /**
         * @return an instance of mouse listener
         * @see BasicTableHeaderUI#createMouseInputListener()
         */
        protected MouseInputListener createMouseInputListener() {
            return new SortedMouseInputHandler();
        }

        /**
         * Paints the given component.
         * 
         * @param g
         *            graphics to paint info
         * @param c
         *            component the paint
         * @see BasicTableHeaderUI#paint(java.awt.Graphics, JComponent)
         */
        public void paint(final Graphics g, final JComponent c) {
            if (header.getColumnModel().getColumnCount() <= 0) {
                return;
            }
            boolean ltr = header.getComponentOrientation().isLeftToRight();

            Rectangle clip = g.getClipBounds();
            Point left = clip.getLocation();
            Point right = new Point(clip.x + clip.width - 1, clip.y);
            TableColumnModel cm = header.getColumnModel();
            int cMin = header.columnAtPoint(ltr ? left : right);
            int cMax = header.columnAtPoint(ltr ? right : left);
            // This should never happen.
            if (cMin == -1) {
                cMin = 0;
            }
            // If the table does not have enough columns to fill the view
            // we'll get -1.
            // Replace this with the index of the last column.
            if (cMax == -1) {
                cMax = cm.getColumnCount() - 1;
            }

            TableColumn draggedColumn = header.getDraggedColumn();
            int columnWidth;
            Rectangle cellRect = header.getHeaderRect(ltr ? cMin : cMax);
            TableColumn aColumn;
            if (ltr) {
                for (int column = cMin; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, column);
                    }
                    cellRect.x += columnWidth;
                }
            } else {
                for (int column = cMax; column >= cMin; column--) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, column);
                    }
                    cellRect.x += columnWidth;
                }
            }

            // Paint the dragged column if we are dragging.
            if (draggedColumn != null) {
                int draggedColumnIndex = viewIndexForColumn(draggedColumn);
                Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex);

                // Draw a gray well in place of the moving column.
                g.setColor(header.getParent().getBackground());
                g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);

                draggedCellRect.x += header.getDraggedDistance();

                // Fill the background.
                g.setColor(header.getBackground());
                g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);

                paintCell(g, draggedCellRect, draggedColumnIndex);
            }

            // Remove all components in the rendererPane.
            rendererPane.removeAll();
        }

        /**
         * Paint cell.
         * 
         * @param g
         *            the graphics to paint into
         * @param cellRect
         *            the cell rectangle
         * @param columnIndex
         *            the column index
         */
        private void paintCell(final Graphics g, final Rectangle cellRect, final int columnIndex) {
            TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
            TableCellRenderer renderer = aColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = header.getDefaultRenderer();
            }

            Component component = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);

            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setHorizontalTextPosition(SwingConstants.LEFT);

                if (!aColumn.getHeaderValue().equals(m_sortingColumn) || m_sortingType == SortType.None) {
                    label.setIcon(null);
                } else if (m_sortingType == SortType.Ascending) {
                    label.setIcon(s_ascendIcon);
                } else
                // if (sortingType SortType.Ascending)
                {
                    label.setIcon(s_descendIcon);
                }
            }

            rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
        }

        /**
         * Calculates a view index for column.
         * 
         * @param aColumn
         *            the column
         * 
         * @return the int
         */
        private int viewIndexForColumn(final TableColumn aColumn) {
            TableColumnModel cm = header.getColumnModel();
            for (int column = 0; column < cm.getColumnCount(); column++) {
                if (cm.getColumn(column) == aColumn) {
                    return column;
                }
            }
            return -1;
        }

        /**
         *
         */
        public class SortedMouseInputHandler extends BasicTableHeaderUI.MouseInputHandler {
            /**
             * Constructor
             */
            public SortedMouseInputHandler() {
                super();
            }

            /**
             * @param e
             *            event
             * @see BasicTableHeaderUI.MouseInputHandler#mouseClicked(java.awt.event.MouseEvent)
             */
            public void mouseClicked(final MouseEvent e) {
                Point p = e.getPoint();
                TableColumnModel columnModel = header.getColumnModel();
                TableModel model = header.getTable().getModel();
                int index = columnModel.getColumnIndexAtX(p.x);

                if (index != -1 && model instanceof ITableModel<?, ?>) {
                    ITableModel<?, ?> tableModel = ((ITableModel<?, ?>) model);
                    Object headerValue = columnModel.getColumn(index).getHeaderValue();

                    if (!m_sortingColumn.equals(headerValue)) {
                        m_sortingColumn = headerValue.toString();
                        m_sortingType = SortType.None;
                    }
                    m_sortingType = m_sortingType.next();
                    tableModel.sortBy(index, m_sortingType);
                }
                header.resizeAndRepaint();
            }
        };
    }

}
