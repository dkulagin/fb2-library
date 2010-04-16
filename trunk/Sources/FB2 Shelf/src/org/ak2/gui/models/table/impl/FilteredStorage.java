/**
 *
 */
package org.ak2.gui.models.table.impl;

import java.util.ArrayList;
import java.util.List;

import org.ak2.gui.models.table.IStorage;
import org.ak2.utils.LengthUtils;

/**
 * @param <Entity>
 *            entity type
 * @author Whippet
 */
public class FilteredStorage<Entity> extends AbstractProxyStorage<Entity> {
    private IEntityFilter<Entity>[] m_filter;

    /**
     * @return the filter
     */
    public IEntityFilter<Entity>[] getFilter() {
        return m_filter;
    }

    /**
     * @param comparator
     *            the comparator to set
     */
    public void setFilter(final IEntityFilter<Entity>... filter) {
        m_filter = filter;
        refresh();
    }

    /**
     * @return list of original indexes
     * @see AbstractProxyStorage#createIndexMap()
     */
    @Override
    protected List<Integer> createIndexMap() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        final IStorage<Entity> original = getOriginal();
        if (original != null) {
            if (LengthUtils.isNotEmpty(m_filter)) {
                for (int originalIndex = 0; originalIndex < original.getEntityCount(); originalIndex++) {
                    final Entity entity = original.getEntity(originalIndex);
                    if (accept(entity)) {
                        list.add(originalIndex);
                    }
                }
            } else {
                for (int originalIndex = 0; originalIndex < original.getEntityCount(); originalIndex++) {
                    list.add(originalIndex);
                }
            }
        }
        return list;
    }

    private boolean accept(Entity entity) {
        for (IEntityFilter<Entity> f : m_filter) {
            if (f != null && !f.accept(entity)) {
                return false;
            }
        }
        return true;
    }
}
