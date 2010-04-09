/**
 *
 */
package org.ak2.gui.models.table.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ak2.gui.models.table.IStorage;

/**
 * @param <Entity>
 *            entity type
 * @author Whippet
 */
public class SortedStorage<Entity> extends AbstractProxyStorage<Entity> {
    private Comparator<Entity> m_comparator;

    /**
     * @return the comparator
     */
    public Comparator<Entity> getComparator() {
        return m_comparator;
    }

    /**
     * @param comparator
     *            the comparator to set
     */
    public void setComparator(final Comparator<Entity> comparator) {
        m_comparator = comparator;
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
            for (int originalIndex = 0; originalIndex < original.getEntityCount(); originalIndex++) {
                list.add(originalIndex);
            }

            if (m_comparator != null) {
                Collections.sort(list, new Comparator<Integer>() {
                    public int compare(final Integer index1, final Integer index2) {
                        Entity entity1 = original.getEntity(index1);
                        Entity entity2 = original.getEntity(index2);
                        return m_comparator.compare(entity1, entity2);
                    }
                });
            }
        }
        return list;
    }
}
