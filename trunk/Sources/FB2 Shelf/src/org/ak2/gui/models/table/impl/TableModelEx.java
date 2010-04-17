package org.ak2.gui.models.table.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.ak2.gui.models.table.IFactory;
import org.ak2.gui.models.table.IStorage;
import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.table.SortType;
import org.ak2.utils.LengthUtils;

/**
 * This class implements read-only table model which gets cell values from a list of entities using reflection.
 *
 * @param <Entity>
 *            entity class
 * @param <EntityContainer>
 *            entity container class
 */
public class TableModelEx<Entity, EntityContainer> extends AbstractTableModel implements ITableModel<Entity, EntityContainer> {
    static final long serialVersionUID = 8641652820732984344L;

    private final int fieldColumnCount;

    private final String[] fieldColumnNames;

    private final ITableColumnAdapter[] fieldAdapters;

    private final IFactory<Entity, EntityContainer> fieldFactory;

    private IStorage<Entity> fieldStorage;

    private FilteredStorage<Entity> fieldFiltered;

    private SortedStorage<Entity> fieldSorted;

    /**
     * Constructor.
     *
     * @param factory
     *            storage factory
     * @param adapters
     *            the list of entity column adapters
     */
    public TableModelEx(final IFactory<Entity, EntityContainer> factory, final ITableColumnAdapter[] adapters) {
        this(factory, null, adapters);
    }

    /**
     * Constructor.
     *
     * @param factory
     *            storage factory
     * @param rootKey
     *            root NLS key for the model
     * @param adapters
     *            the list of entity column adapters
     */
    public TableModelEx(final IFactory<Entity, EntityContainer> factory, final String[] columnNames, final ITableColumnAdapter[] adapters) {
        fieldFactory = factory;

        fieldColumnCount = LengthUtils.length(adapters);

        fieldAdapters = adapters;

        fieldColumnNames = new String[fieldColumnCount];

        int i = 0;
        for (int n = Math.min(LengthUtils.length(columnNames), fieldColumnNames.length); i < n; i++) {
            fieldColumnNames[i] = LengthUtils.safeString(columnNames[i], "Column " + i);
        }
        for (; i < fieldColumnNames.length; i++) {
            fieldColumnNames[i] = "Column " + i;
        }
    }

    /**
     * @return new instance
     * @see ITableModel#newInstance()
     */
    public Entity newInstance() {
        return fieldFactory.newInstance();
    }

    /**
     * Sort by the given column.
     *
     * @param columnIndex
     *            the column index
     * @param type
     *            sort type
     * @see ITableModel#sortBy(int, boolean)
     */
    public void sortBy(final int columnIndex, final SortType type) {
        beforeEntitiesSet();

        if (type != SortType.None && columnIndex != -1) {
            getSortedStorage().setComparator(new ColumnComparator(columnIndex, type == SortType.Ascending));
        } else {
            getSortedStorage().setComparator(null);
        }

        afterEntitiesSet();
        fireTableDataChanged();
    }

    public void setFilter(final IEntityFilter<Entity>... filter) {
        beforeEntitiesSet();

        getFilteredStorage().setFilter(filter);
        getSortedStorage().refresh();

        afterEntitiesSet();
        fireTableDataChanged();
    }

    /**
     * Sets data to model
     *
     * @param container
     *            data container
     * @see ITableModel#setData(java.lang.Object)
     */
    public void setData(final EntityContainer container) {
        setStorage(fieldFactory.newStorage(container));
    }

    /**
     * @return entity storage
     */
    protected IStorage<Entity> getStorage() {
        return getSortedStorage();
    }

    /**
     * @return entity storage
     */
    protected SortedStorage<Entity> getSortedStorage() {
        if (fieldSorted == null) {
            fieldSorted = new SortedStorage<Entity>();
            fieldSorted.setOriginal(getFilteredStorage());
        }
        return fieldSorted;
    }

    /**
     * @return entity storage
     */
    protected FilteredStorage<Entity> getFilteredStorage() {
        if (fieldFiltered == null) {
            fieldFiltered = new FilteredStorage<Entity>();
            fieldFiltered.setOriginal(getOriginalStorage());
        }
        return fieldFiltered;
    }

    protected IStorage<Entity> getOriginalStorage() {
        if (fieldStorage == null) {
            fieldStorage = fieldFactory.newStorage(null);
        }
        return fieldStorage;
    }

    /**
     * @param storage
     *            entity storage to set
     */
    protected void setStorage(final IStorage<Entity> storage) {
        beforeEntitiesSet();
        fieldStorage = storage;
        getFilteredStorage().setOriginal(fieldStorage);
        getSortedStorage().setOriginal(getFilteredStorage());
        afterEntitiesSet();

        fireTableDataChanged();
    }

    /**
     * Called before new storage will be set.
     */
    protected void beforeEntitiesSet() {
    }

    /**
     * Called after new storage will be set.
     */
    protected void afterEntitiesSet() {
    }

    /**
     * Adds entity.
     *
     * @param entity
     *            new entity
     * @see ITableModel#addEntity(java.lang.Object)
     */
    public void addEntity(final Entity entity) {
        final int realRow = getRowCount();
        beforeEntityAdded(realRow, entity);
        getStorage().addEntity(realRow, entity);
        afterEntityAdded(realRow, entity);
        fireTableRowsInserted(realRow, realRow);
    }

    /**
     * Called before entity will be added.
     *
     * @param row
     *            row to add into
     * @param entity
     *            entity to add
     */
    protected void beforeEntityAdded(final int row, final Entity entity) {
    }

    /**
     * Called after entity has been added.
     *
     * @param realRow
     *            real entity index
     * @param entity
     *            addded entity
     */
    protected void afterEntityAdded(final int realRow, final Entity entity) {
    }

    /**
     * Set entity instead of existing.
     *
     * @param row
     *            row to change
     * @param entity
     *            new entity
     * @see ITableModel#setEntity(int, java.lang.Object)
     */
    public void setEntity(final int row, final Entity entity) {
        if (row >= 0 && row < getRowCount()) {
            beforeEntitySet(row, entity);
            getStorage().setEntity(row, entity);
            afterEntitySet(row, entity);
            fireTableRowsUpdated(row, row);
        }
    }

    /**
     * Swaps two entities
     *
     * @param row1
     *            index of first entity
     * @param row2
     *            index of second entity
     */
    public void swap(final int row1, final int row2) {
        final int entityCount = getStorage().getEntityCount();
        final boolean valid1 = row1 >= 0 && row1 < entityCount;
        final boolean valid2 = row2 >= 0 && row2 < entityCount;
        if (valid1 && valid2) {
            getStorage().swap(row1, row2);
            fireTableRowsUpdated(Math.min(row1, row2), Math.max(row1, row2));
        }
    }

    /**
     * Update entities.
     *
     * @param handler
     *            the entity handler
     * @param rows
     *            the selected rows
     * @see ITableModel#updateEntities(IEntityHandler, int[])
     */
    public void updateEntities(final IEntityHandler<Entity> handler, final int... rows) {
        if (LengthUtils.isNotEmpty(rows)) {
            for (final int rowIndex : rows) {
                updateEntity(rowIndex, handler);
            }
        } else {
            for (int rowIndex = 0, rowCount = getRowCount(); rowIndex < rowCount; rowIndex++) {
                updateEntity(rowIndex, handler);
            }
        }
    }

    /**
     * Update entity.
     *
     * @param rowIndex
     *            the row index
     * @param handler
     *            the handler
     */
    protected void updateEntity(final int rowIndex, final IEntityHandler<Entity> handler) {
        final Entity object = getStorage().getEntity(rowIndex);
        if (handler.accept(object)) {
            beforeEntitySet(rowIndex, object);
            if (handler.handle(rowIndex, object)) {
                afterEntitySet(rowIndex, object);
                fireTableRowsUpdated(rowIndex, rowIndex);
            }
        }
    }

    /**
     * Called before entity is set.
     *
     * @param row
     *            row to set into
     * @param entity
     *            entity to set
     */
    protected void beforeEntitySet(final int row, final Entity entity) {
    }

    /**
     * Called after entity is set.
     *
     * @param row
     *            row to set into
     * @param entity
     *            entity to set
     */
    protected void afterEntitySet(final int row, final Entity entity) {
    }

    /**
     * Remove all entities from the model
     */
    public void removeAllEntities() {
        final int rows = getRowCount();
        if (rows > 0) {
            beforeTableCleared();
            getStorage().clear();
            afterTableCleared();
            fireTableRowsDeleted(0, rows - 1);
        }
    }

    /**
     *
     */
    protected void beforeTableCleared() {
    }

    /**
     *
     */
    protected void afterTableCleared() {
    }

    /**
     * Remove entity from the model.
     *
     * @param row
     *            row to delete
     * @see ITableModel#removeEntity(int)
     */
    public void removeEntity(final int row) {
        if (row >= 0 && row < getRowCount()) {
            final Entity object = getStorage().getEntity(row);
            removeEntity(row, object);
        }
    }

    /**
     * Remove entity from the model
     *
     * @param row
     *            row to delete
     */
    public void removeEntity(final Entity entity) {
        removeEntity(getRowOf(entity));
    }

    /**
     * Remove entities from the model
     *
     * @param rows
     *            rows to delete
     */
    public void removeEntities(final int[] rows) {
        for (int i = LengthUtils.length(rows) - 1; i >= 0; i--) {
            final Entity object = getStorage().getEntity(rows[i]);
            removeEntity(rows[i], object);
        }
    }

    /**
     * Remove entities from the model
     *
     * @param rows
     *            rows to delete
     * @param removeFilter
     *            entity filter accepting entities to remove
     * @see ITableModel#removeEntities(IEntityFilter, int[])
     */
    public void removeEntities(final IEntityFilter<Entity> removeFilter, final int... rows) {
        if (LengthUtils.isNotEmpty(rows)) {
            for (int i = LengthUtils.length(rows) - 1; i >= 0; i--) {
                removeEntity(rows[i], removeFilter);
            }
        } else {
            for (int rowIndex = getRowCount() - 1; rowIndex >= 0; rowIndex--) {
                removeEntity(rowIndex, removeFilter);
            }
        }
    }

    /**
     * Removes the entity.
     *
     * @param rowIndex
     *            the row index
     * @param removeFilter
     *            the remove filter
     */
    protected void removeEntity(final int rowIndex, final IEntityFilter<Entity> removeFilter) {
        final Entity object = getStorage().getEntity(rowIndex);
        if (removeFilter.accept(object)) {
            removeEntity(rowIndex, object);
        }
    }

    /**
     * Removes the entity.
     *
     * @param rowIndex
     *            the row index
     * @param object
     *            the object to remove
     */
    protected void removeEntity(final int rowIndex, final Entity object) {
        beforeEntityRemoved(rowIndex, object);
        getStorage().removeEntity(rowIndex);
        afterEntityRemoved(rowIndex, object);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    /**
     * Called before entity is removed
     *
     * @param row
     *            row to remove from
     * @param entity
     *            entity to remove
     */
    protected void beforeEntityRemoved(final int row, final Entity entity) {
    }

    /**
     * Called after entity is removed
     *
     * @param row
     *            old entity index
     * @param entity
     *            removed entity
     */
    protected void afterEntityRemoved(final int row, final Entity entity) {
    }

    /**
     * Gets an entity for the given index.
     *
     * @param rowIndex
     *            the entity index
     * @return corresponding entity or null.
     * @see ITableModel#getEntity(int)
     */
    public Entity getEntity(final int rowIndex) {
        if (rowIndex >= 0 && rowIndex < getRowCount()) {
            return getStorage().getEntity(rowIndex);
        }
        return null;
    }

    /**
     * Gets the entities.
     *
     * @return the entities
     * @see ITableModel#getEntities()
     */
    public List<Entity> getEntities() {
        return getStorage().getEntities();
    }

    /**
     * Returns the index of row contained the given entity
     *
     * @param entity
     *            entity to find
     * @return index or -1
     */
    public int getRowOf(final Entity entity) {
        return getStorage().getRowOf(entity);
    }

    /**
     * Returns row tooltip for the given row index
     *
     * @param row
     *            row index
     * @return string
     * @see ITableModel#getTooltip(int)
     */
    public String getTooltip(final int row) {
        return null;
    }

    /**
     * Returns the number of rows in the model.
     *
     * @return number of rows
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return getStorage().getEntityCount();
    }

    /**
     * Returns the number of columns in the model.
     *
     * @return number of columns
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return fieldColumnCount;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex
     *            the column being queried
     * @return class of data contained by the given column
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return fieldAdapters[columnIndex].getCellClass();
    }

    /**
     * Returns a default name for the column.
     *
     * @param column
     *            column index
     * @return column name
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(final int column) {
        return fieldColumnNames[column];
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex
     *            the row whose value is to be queried
     * @param columnIndex
     *            the column whose value is to be queried
     * @return the value Object at the specified cell
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final Object res = "";
        if (columnIndex >= 0 && columnIndex < getColumnCount()) {
            final Entity entity = getEntity(rowIndex);
            return getCellValue(rowIndex, columnIndex, entity);
        }
        return res;
    }

    /**
     * @param rowIndex
     *            the row whose value is to be queried
     * @param columnIndex
     *            the column whose value is to be queried
     * @param entity
     *            original entity
     * @return a cell value
     */
    protected Object getCellValue(final int rowIndex, final int columnIndex, final Entity entity) {
        final Object value = transformEntity(rowIndex, entity);
        return fieldAdapters[columnIndex].getCellValue(this, rowIndex, columnIndex, value);
    }

    /**
     * Transforms entity before getting cell value
     *
     * @param rowIndex
     *            entity index
     * @param entity
     *            entity
     * @return object
     */
    protected Object transformEntity(final int rowIndex, final Entity entity) {
        return entity;
    }

    /**
     * @return the adapters
     */
    protected final ITableColumnAdapter[] getAdapters() {
        return fieldAdapters;
    }

    /**
     *
     */
    private class ColumnComparator implements Comparator<Entity> {
        private final int m_columnIndex;

        private final boolean m_ascending;

        /**
         * Constructor
         *
         * @param columnIndex
         *            column index
         * @param ascending
         *            comparation type
         */
        public ColumnComparator(final int columnIndex, final boolean ascending) {
            super();
            m_columnIndex = columnIndex;
            m_ascending = ascending;
        }

        /**
         * @param entity1
         *            the first object to be compared.
         * @param entity2
         *            the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final Entity entity1, final Entity entity2) {
            final Object cellValue1 = getCellValue(0, m_columnIndex, entity1);
            final Object cellValue2 = getCellValue(0, m_columnIndex, entity2);

            int result = compareCellValues(cellValue1, cellValue2);
            return m_ascending ? result : -result;
        }

        /**
         * @param cellValue1
         *            the first object to be compared.
         * @param cellValue2
         *            the first object to be compared.
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
         */
        @SuppressWarnings("unchecked")
        private int compareCellValues(final Object cellValue1, final Object cellValue2) {
            int result = 0;
            if (cellValue1 == null && cellValue2 == null) {
                result = 0;
            } else if (cellValue1 != null && cellValue2 == null) {
                result = 1;
            } else if (cellValue1 == null && cellValue2 != null) {
                result = -1;
            } else if (cellValue1 instanceof Date && cellValue2 instanceof Date) {
                result = compareNumbers(((Date) cellValue1).getTime(), ((Date) cellValue2).getTime());
            } else if (cellValue1 instanceof Number && cellValue2 instanceof Number) {
                result = compareNumbers(((Number) cellValue1).longValue(), ((Number) cellValue2).longValue());
            } else if (cellValue1 instanceof String && cellValue2 instanceof String) {
                result = compareStrings((String) cellValue1, (String) cellValue2);
            } else if (cellValue1.getClass() == cellValue2.getClass() && cellValue1 instanceof Comparable<?>) {
                result = ((Comparable) cellValue1).compareTo(cellValue2);
            } else {
                final String s1 = cellValue1.toString();
                final String s2 = cellValue2.toString();
                result = compareStrings(s1, s2);
            }
            return result;
        }

        /**
         * Compare strings.
         *
         * @param s1
         *            the s1
         * @param s2
         *            the s2
         *
         * @return the int
         */
        private int compareStrings(final String s1, final String s2) {
            return (int) Math.signum(s1.toUpperCase().compareTo(s2.toUpperCase()));
        }

        /**
         * Compare numbers.
         *
         * @param n1
         *            first number
         * @param n2
         *            second number
         *
         * @return the int
         */
        private int compareNumbers(final long n1, final long n2) {
            return n1 == n2 ? 0 : n1 < n2 ? -1 : 1;
        }

    }

}
