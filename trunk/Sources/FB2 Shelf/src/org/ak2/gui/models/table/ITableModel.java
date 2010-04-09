package org.ak2.gui.models.table;

import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.table.impl.IEntityHandler;

/**
 * @param <Entity>
 *            entity class
 * @param <EntityContainer>
 *            entity container class
 * 
 * @author Alexander Kasatkin
 */
public interface ITableModel<Entity, EntityContainer> extends TableModel {
    /**
     * @return new instance
     */
    public Entity newInstance();

    /**
     * Gets an entity for the given index.
     * 
     * @param rowIndex
     *            the entity index
     * @return corresponding entity or null.
     */
    public Entity getEntity(final int rowIndex);

    /**
     * Gets the entities.
     * 
     * @return the entities
     */
    public List<Entity> getEntities();

    /**
     * Returns the index of row contained the given entity
     * 
     * @param entity
     *            entity to find
     * @return index or -1
     */
    public int getRowOf(final Entity entity);

    /**
     * Sort by the given column.
     * 
     * @param columnIndex
     *            the column index
     * @param type
     *            sort type
     */
    public void sortBy(final int columnIndex, SortType type);

    /**
     * Returns row tooltip for the given row index
     * 
     * @param row
     *            row index
     * @return string
     */
    public String getTooltip(final int row);

    /**
     * Sets data to model
     * 
     * @param container
     *            data container
     */
    public void setData(final EntityContainer container);

    /**
     * Adds entity
     * 
     * @param entity
     *            new entity
     */
    public void addEntity(final Entity entity);

    /**
     * Set entity insead of existing
     * 
     * @param row
     *            row to change
     * @param entity
     *            new entity
     */
    public void setEntity(final int row, final Entity entity);

    /**
     * Swaps two entities
     * 
     * @param row1
     *            index of first entity
     * @param row2
     *            index of second entity
     */
    public void swap(final int row1, final int row2);

    /**
     * Update entities.
     * 
     * @param handler
     *            the entity handler
     * @param rows
     *            the selected rows
     */
    public void updateEntities(final IEntityHandler<Entity> handler, final int... rows);

    /**
     * Remove all entities from the model
     * 
     */
    public void removeAllEntities();

    /**
     * Remove entity from the model
     * 
     * @param row
     *            row to delete
     */
    public void removeEntity(final Entity entity);

    /**
     * Remove entity from the model
     * 
     * @param row
     *            row to delete
     */
    public void removeEntity(final int row);

    /**
     * Remove entities from the model
     * 
     * @param rows
     *            rows to delete
     */
    public void removeEntities(final int[] rows);

    /**
     * Remove entities from the model
     * 
     * @param removeFilter
     *            entity filter accepting entities to remove
     * @param rows
     *            rows to delete
     */
    public void removeEntities(final IEntityFilter<Entity> removeFilter, final int... rows);

    /**
     * Notifies all listeners that all cell values in the table's rows may have changed. The number of rows may also have changed and the <code>JTable</code>
     * should redraw the table from scratch. The structure of the table (as in the order of the columns) is assumed to be the same.
     * 
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged();

    /**
     * Notifies all listeners that the table's structure has changed. The number of columns in the table, and the names and types of the new columns may be
     * different from the previous state. If the <code>JTable</code> receives this event and its <code>autoCreateColumnsFromModel</code> flag is set it discards
     * any table columns that it had and reallocates default columns in the order they appear in the model. This is the same as calling
     * <code>setModel(TableModel)</code> on the <code>JTable</code>.
     * 
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableStructureChanged();

    /**
     * Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     * 
     * @param firstRow
     *            the first row
     * @param lastRow
     *            the last row
     * 
     * @see TableModelEvent
     * @see EventListenerList
     * 
     */
    public void fireTableRowsInserted(int firstRow, int lastRow);

    /**
     * Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     * 
     * @param firstRow
     *            the first row
     * @param lastRow
     *            the last row
     * 
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated(int firstRow, int lastRow);

    /**
     * Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     * 
     * @param firstRow
     *            the first row
     * @param lastRow
     *            the last row
     * 
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow);

    /**
     * Notifies all listeners that the value of the cell at <code>[row, column]</code> has been updated.
     * 
     * @param row
     *            row of cell which has been updated
     * @param column
     *            column of cell which has been updated
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableCellUpdated(int row, int column);

    /**
     * Forwards the given notification event to all <code>TableModelListeners</code> that registered themselves as listeners for this table model.
     * 
     * @param e
     *            the event to be forwarded
     * 
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged(TableModelEvent e);

}
