/**
 *
 */
package org.ak2.gui.models.table.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.ak2.gui.models.table.IFactory;
import org.ak2.gui.models.table.IStorage;
import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.utils.LengthUtils;

/**
 * @author Whippet
 */
public class CompositeTableModel<Entity> extends TableModelEx<Entity, List<ITableModel<Entity, ?>>> {
    private static final long serialVersionUID = 5359642786956347446L;

    private final Listener m_listener = new Listener();

    private AtomicBoolean m_eventLock = new AtomicBoolean(false);

    /**
     * Constructor
     *
     * @param factory
     * @param columns
     * @param adapters
     */
    public CompositeTableModel(final String[] columns, final ITableColumnAdapter[] adapters) {
        super(new Factory<Entity>(), columns, adapters);
    }

    /**
     * Constructor
     *
     * @param factory
     * @param columns
     * @param adapters
     */
    public CompositeTableModel(final TableModelEx<Entity, ?> model) {
        super(new Factory<Entity>(), model.getColumnNames(), model.getAdapters());
        List<ITableModel<Entity, ?>> models = new ArrayList<ITableModel<Entity, ?>>(1);
        models.add(model);
        setData(models);
    }

    /**
     * @param row
     * @return
     * @see org.ak2.gui.models.table.impl.TableModelEx#getTooltip(int)
     */
    @Override
    public String getTooltip(final int row) {
        final Storage<Entity> originalStorage = getCompositeStorage();
        if (originalStorage != null) {
            final int originalIndex = AbstractProxyStorage.getOriginalIndex(getStorage(), row);
            return originalStorage.getTooltip(originalIndex);
        }

        return null;
    }

    /**
     * Sets data to model
     *
     * @param container
     *            data container
     * @see TableModelEx#setData(java.lang.Object)
     */
    @Override
    public void setData(final List<ITableModel<Entity, ?>> container) {
        if (LengthUtils.isNotEmpty(container)) {
            final List<ITableModel<Entity, ?>> models = new ArrayList<ITableModel<Entity, ?>>(container.size());
            fillModelList(models, container);
            super.setData(models);
        } else {
            super.setData(container);
        }
    }

    /**
     * Remove all entities from the model
     *
     * @see TableModelEx#removeAllEntities()
     */
    @Override
    public void removeAllEntities() {
        int rows = getRowCount();

        if (rows > 0) {
            m_eventLock.set(true);
            try {
                beforeTableCleared();
                getStorage().clear();
                afterTableCleared();
                fireTableRowsDeleted(0, rows - 1);
            } finally {
                m_eventLock.set(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void fillModelList(final List<ITableModel<Entity, ?>> target, final List<? extends ITableModel<Entity, ?>> source) {
        for (final ITableModel<Entity, ?> tableModel : source) {
            if (tableModel instanceof CompositeTableModel) {
                final CompositeTableModel<Entity> cmtm = (CompositeTableModel<Entity>) tableModel;
                final Storage<Entity> originalStorage = cmtm.getCompositeStorage();
                if (originalStorage != null) {
                    fillModelList(target, originalStorage.getModels());
                }
            } else {
                target.add(tableModel);
            }
        }
    }

    /**
     *
     * @see org.ak2.gui.models.table.impl.TableModelEx#beforeEntitiesSet()
     */
    @Override
    protected void beforeEntitiesSet() {
        final Storage<Entity> originalStorage = getCompositeStorage();
        if (originalStorage != null) {
            originalStorage.removeListener(m_listener);
        }
    }

    /**
     *
     * @see org.ak2.gui.models.table.impl.TableModelEx#afterEntitiesSet()
     */
    @Override
    protected void afterEntitiesSet() {
        final Storage<Entity> originalStorage = getCompositeStorage();
        if (originalStorage != null) {
            originalStorage.addListener(m_listener);
        }
    }

    private Storage<Entity> getCompositeStorage() {
        return (Storage<Entity>) getOriginalStorage();
    }

    private static class Factory<Entity> implements IFactory<Entity, List<ITableModel<Entity, ?>>> {
        public Entity newInstance() {
            return null;
        }

        public IStorage<Entity> newStorage(final List<ITableModel<Entity, ?>> container) {
            return new Storage<Entity>(container);
        }
    }

    private static class Storage<Entity> implements IStorage<Entity> {
        private List<? extends ITableModel<Entity, ?>> m_models = Collections.emptyList();

        /**
         * Constructor
         *
         * @param models
         *            list of child models
         */
        public Storage(final List<? extends ITableModel<Entity, ?>> models) {
            if (models != null) {
                m_models = models;
            }
        }

        /**
         * Gets the models.
         *
         * @return the models
         */
        public synchronized List<? extends ITableModel<Entity, ?>> getModels() {
            return m_models;
        }

        /**
         * Adds the listener.
         *
         * @param listener
         *            the listener
         */
        public void addListener(final TableModelListener listener) {
            for (final ITableModel<Entity, ?> model : m_models) {
                model.addTableModelListener(listener);
            }
        }

        /**
         * Removes the listener.
         *
         * @param listener
         *            the listener
         */
        public void removeListener(final TableModelListener listener) {
            for (final ITableModel<Entity, ?> model : m_models) {
                model.removeTableModelListener(listener);
            }
        }

        /**
         * Gets the entities.
         *
         * @return the entities
         * @see IStorage#getEntities()
         */
        public synchronized List<Entity> getEntities() {
            final ArrayList<Entity> list = new ArrayList<Entity>();
            for (final ITableModel<Entity, ?> model : m_models) {
                list.addAll(model.getEntities());
            }

            return list;
        }

        /**
         * Gets the tooltip for the given row.
         *
         * @param rowIndex
         *            the row index
         *
         * @return the tooltip
         */
        public synchronized String getTooltip(final int rowIndex) {
            int startIndex = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                final int rowCount = model.getRowCount();
                final int nextStartIndex = startIndex + rowCount;
                if (startIndex <= rowIndex && rowIndex < nextStartIndex) {
                    return model.getTooltip(rowIndex - startIndex);
                }
                startIndex = nextStartIndex;
            }
            return null;
        }

        /**
         * Adds entity to the model. Not implemented.
         *
         * @param row
         *            row to insert
         * @param entity
         *            new entity
         * @return row of inserted entity
         * @see IStorage#addEntity(int, java.lang.Object)
         */
        @Deprecated
        public int addEntity(final int row, final Entity entity) {
            return -1;
        }

        /**
         * Remove all entities from the storage
         *
         * @see IStorage#clear()
         */
        public synchronized void clear() {
            for (final ITableModel<Entity, ?> model : m_models) {
                model.removeAllEntities();
            }
        }

        /**
         * Gets an entity for the given index.
         *
         * @param rowIndex
         *            the entity index
         * @return corresponding entity or null.
         * @see IStorage#getEntity(int)
         */
        public synchronized Entity getEntity(final int rowIndex) {
            Entity result = null;
            int startIndex = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                final int rowCount = model.getRowCount();
                final int nextStartIndex = startIndex + rowCount;
                if (startIndex <= rowIndex && rowIndex < nextStartIndex) {
                    result = model.getEntity(rowIndex - startIndex);
                    break;
                }
                startIndex = nextStartIndex;
            }
            return result;
        }

        /**
         * Returns the number of entities in the storage.
         *
         * @return number of rows
         * @see IStorage#getEntityCount()
         */
        public synchronized int getEntityCount() {
            int count = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                count += model.getRowCount();
            }
            return count;
        }

        /**
         * Returns the index of row contained the given entity
         *
         * @param entity
         *            entity to find
         * @return index or -1
         * @see IStorage#getRowOf(java.lang.Object)
         */
        public synchronized int getRowOf(final Entity entity) {
            int startIndex = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                final int rowOf = model.getRowOf(entity);
                if (rowOf > -1) {
                    return startIndex + rowOf;
                }
                startIndex += model.getRowCount();
            }
            return -1;
        }

        /**
         * Remove entity from the storage
         *
         * @param row
         *            row to delete
         * @see IStorage#removeEntity(int)
         */
        public synchronized void removeEntity(final int rowIndex) {
            int startIndex = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                final int rowCount = model.getRowCount();
                final int nextStartIndex = startIndex + rowCount;
                if (startIndex <= rowIndex && rowIndex < nextStartIndex) {
                    model.removeEntity(rowIndex - startIndex);
                    break;
                }
                startIndex = nextStartIndex;
            }
        }

        /**
         * Set entity instead of existing
         *
         * @param row
         *            row to change
         * @param entity
         *            new entity
         * @see IStorage#setEntity(int, java.lang.Object)
         */
        public synchronized void setEntity(final int rowIndex, final Entity entity) {
            int startIndex = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                final int rowCount = model.getRowCount();
                final int nextStartIndex = startIndex + rowCount;
                if (startIndex <= rowIndex && rowIndex < nextStartIndex) {
                    model.setEntity(rowIndex - startIndex, entity);
                    break;
                }
                startIndex = nextStartIndex;
            }
        }

        /**
         * Swaps two entities. Not implemented.
         *
         * @param row1
         *            index of first entity
         * @param row2
         *            index of second entity
         * @see IStorage#swap(int, int)
         */
        @Deprecated
        public void swap(final int row1, final int row2) {
        }

        /**
         * Gets the start index for the given inner model.
         *
         * @param originalModel
         *            the original model
         *
         * @return the start index
         */
        private int getStartIndex(final TableModel originalModel) {
            int startIndex = 0;
            for (final ITableModel<Entity, ?> model : m_models) {
                if (model == originalModel) {
                    return startIndex;
                }
                startIndex += model.getRowCount();
            }
            return -1;
        }
    }

    private class Listener implements TableModelListener {
        /**
         * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed.
         *
         * @param e
         *            table event
         * @see TableModelListener#tableChanged(TableModelEvent)
         */
        @SuppressWarnings("unchecked")
        public void tableChanged(final TableModelEvent e) {
            if (!m_eventLock.get()) {
                final int firstChildRow = e.getFirstRow();
                final int lastChildRow = e.getLastRow();
                final int column = e.getColumn();
                final int type = e.getType();

                if (firstChildRow == TableModelEvent.HEADER_ROW) {
                    fireTableChanged(create(firstChildRow, lastChildRow, column, type));
                } else {
                    final Storage<Entity> storage = getCompositeStorage();
                    final ITableModel<Entity, ?> childModel = (ITableModel<Entity, ?>) e.getSource();
                    final int startIndex = storage.getStartIndex(childModel);

                    final SortedStorage<Entity> sortingStorage = getSortedStorage();
                    final Set<Integer> indexes = new TreeSet<Integer>();

                    if (type == TableModelEvent.INSERT || type == TableModelEvent.UPDATE) {
                        sortingStorage.refresh();

                        if (lastChildRow - firstChildRow < 10) {
                            for (int childRowIndex = firstChildRow; childRowIndex <= lastChildRow; childRowIndex++) {
                                final int originalIndex = startIndex + childRowIndex;
                                final int virtualIndex = AbstractProxyStorage.getVirtualIndex(sortingStorage, originalIndex);
                                indexes.add(virtualIndex);
                            }

                            final Integer[] indexesArray = indexes.toArray(new Integer[indexes.size()]);
                            for (Integer rowIndex : indexesArray) {
                                fireTableChanged(create(rowIndex, rowIndex, column, type));
                            }
                        } else {
                            fireTableChanged(new TableModelEvent(CompositeTableModel.this));
                        }
                    } else if (type == TableModelEvent.DELETE) {
                        sortingStorage.refresh();
                        fireTableChanged(new TableModelEvent(CompositeTableModel.this));
                    }
                }
            }
        }

        private TableModelEvent create(int type, int firstRow, int lastRow, int column) {
            return new TableModelEvent(CompositeTableModel.this, firstRow, lastRow, column, type);
        }
    }
}