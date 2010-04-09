package org.ak2.gui.models.table.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.ak2.gui.models.table.IStorage;
import org.ak2.utils.LengthUtils;

public abstract class AbstractProxyStorage<Entity> implements IStorage<Entity> {
    private IStorage<Entity> m_original;

    private List<Integer> m_indexMap;

    /**
     * @return the original
     */
    public IStorage<Entity> getOriginal() {
        return m_original;
    }

    /**
     * @param original
     *            the original to set
     */
    public void setOriginal(final IStorage<Entity> original) {
        m_original = original;
        m_indexMap = createIndexMap();
    }

    /**
     * Adds entity to the given row
     *
     * @param row
     *            row index
     * @param entity
     *            entity to add
     * @return index of added entity
     * @see IStorage#addEntity(int, java.lang.Object)
     */
    public int addEntity(final int row, final Entity entity) {
        final int originalIndex = m_original.getEntityCount();
        m_original.addEntity(originalIndex, entity);
        m_indexMap = createIndexMap();
        return getVirtualIndex(originalIndex);
    }

    /**
     * Remove all entities from the storage.
     *
     * @see IStorage#clear()
     */
    public void clear() {
        m_original.clear();
        m_indexMap = Collections.emptyList();
    }

    /**
     * Gets the entities.
     *
     * @return the entities
     * @see IStorage#getEntities()
     */
    public List<Entity> getEntities() {
        final ArrayList<Entity> list = new ArrayList<Entity>();
        for (final int originalIndex : m_indexMap) {
            list.add(m_original.getEntity(originalIndex));
        }
        return list;
    }

    /**
     * Gets an entity for the given index.
     *
     * @param rowIndex
     *            the entity index
     * @return corresponding entity or null.
     * @see IStorage#getEntity(int)
     */
    public Entity getEntity(final int rowIndex) {
        return m_original.getEntity(getOriginalIndex(rowIndex));
    }

    /**
     * Returns the number of entities in the storage.
     *
     * @return number of rows
     * @see IStorage#getEntityCount()
     */
    public int getEntityCount() {
        return LengthUtils.length(m_indexMap);
    }

    /**
     * Returns the index of row contained the given entity
     *
     * @param entity
     *            entity to find
     * @return index or -1
     * @see IStorage#getRowOf(java.lang.Object)
     */
    public int getRowOf(final Entity entity) {
        return getVirtualIndex(m_original.getRowOf(entity));
    }

    /**
     * Remove entity from the storage
     *
     * @param row
     *            row to delete
     * @see IStorage#removeEntity(int)
     */
    public void removeEntity(final int row) {
        m_original.removeEntity(getOriginalIndex(row));
        m_indexMap = createIndexMap();
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
    public void setEntity(final int row, final Entity entity) {
        m_original.setEntity(getOriginalIndex(row), entity);
        m_indexMap = createIndexMap();
    }

    /**
     * Swaps two entities
     *
     * @param row1
     *            index of first entity
     * @param row2
     *            index of second entity
     * @see IStorage#swap(int, int)
     */
    public void swap(final int row1, final int row2) {
        m_original.swap(getOriginalIndex(row1), getOriginalIndex(row2));
    }

    /**
     * Refresh.
     */
    public final void refresh() {
        final IStorage<Entity> original = getOriginal();
        if (original instanceof AbstractProxyStorage) {
            ((AbstractProxyStorage<?>) original).refresh();
        }
        m_indexMap = createIndexMap();
    }

    /**
     * Creates the index map.
     *
     * @return list of original indexes
     */
    protected abstract List<Integer> createIndexMap();

    /**
     * Gets the original index.
     *
     * @param virtualIndex
     *            the virtual index
     *
     * @return the original index
     */
    private final int getOriginalIndex(final int virtualIndex) {
        if (virtualIndex >= 0 && virtualIndex < LengthUtils.length(m_indexMap)) {
            return m_indexMap.get(virtualIndex);
        }
        return -1;
    }

    /**
     * Gets the virtual index.
     *
     * @param originalIndex
     *            the original index
     *
     * @return the virtual index
     */
    private final int getVirtualIndex(final int originalIndex) {
        if (originalIndex != -1) {
            for (int virtualIndex = 0; virtualIndex < LengthUtils.length(m_indexMap); virtualIndex++) {
                if (m_indexMap.get(virtualIndex) == originalIndex) {
                    return virtualIndex;
                }
            }
        }
        return -1;
    }

    public static <Entity> int getOriginalIndex(final IStorage<Entity> storage, final int virtualIndex) {
        int result = virtualIndex;
        IStorage<Entity> next = storage;
        while (next instanceof AbstractProxyStorage) {
            final AbstractProxyStorage<Entity> current = (AbstractProxyStorage<Entity>) next;
            result = current.getOriginalIndex(result);
            next = current.getOriginal();
        }

        return result;
    }

    public static <Entity> int getVirtualIndex(final IStorage<Entity> storage, final int originalIndex) {
        final Stack<AbstractProxyStorage<Entity>> stack = new Stack<AbstractProxyStorage<Entity>>();
        IStorage<Entity> next = storage;
        while (next instanceof AbstractProxyStorage) {
            final AbstractProxyStorage<Entity> current = (AbstractProxyStorage<Entity>) next;
            stack.push(current);
            next = current.getOriginal();
        }

        int result = originalIndex;
        while (!stack.isEmpty()) {
            final AbstractProxyStorage<Entity> current = stack.pop();
            result = current.getVirtualIndex(result);
        }

        return result;
    }
}
