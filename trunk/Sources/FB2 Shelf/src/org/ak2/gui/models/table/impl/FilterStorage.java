/**
 *
 */
package org.ak2.gui.models.table.impl;

import java.util.ArrayList;
import java.util.List;

import org.ak2.gui.models.table.IStorage;

/**
 * @param <Entity>
 *            entity type
 * @author Whippet
 */
public class FilterStorage<Entity> extends AbstractProxyStorage<Entity> {
    private IEntityFilter<Entity> m_filter;

    /**
     * @return the filter
     */
    public IEntityFilter<Entity> getFilter() {
        return m_filter;
    }

    /**
     * @param filter
     *            the filter to set
     */
    public void setFilter(final IEntityFilter<Entity> filter) {
        m_filter = filter;
        refresh();
    }

    /**
     * Creates the index map.
     * 
     * @return the int[]
     * @see org.ak2.gui.models.table.impl.AbstractProxyStorage#createIndexMap()
     */
    @Override
    protected List<Integer> createIndexMap() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        IStorage<Entity> original = getOriginal();
        for (int originalIndex = 0; originalIndex < original.getEntityCount(); originalIndex++) {
            if (m_filter == null || m_filter.accept(original.getEntity(originalIndex))) {
                list.add(originalIndex);
            }
        }

        return list;
    }
}
