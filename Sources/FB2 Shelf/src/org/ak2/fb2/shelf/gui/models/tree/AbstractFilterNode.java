package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.utils.LengthUtils;

public abstract class AbstractFilterNode<T> extends AbstractTreeNode<T> implements IEntityFilter<BookInfo> {

    protected AbstractFilterNode(final AbstractTreeModel model, final T userObject) {
        super(model, userObject);
    }

    @Override
    public final boolean accept(final BookInfo entity) {
        final T userObject = getObject();
        if (userObject == null) {
            return true;
        }
        return accept(entity, userObject);
    }

    protected abstract boolean accept(BookInfo entity, T userObject);

    @Override
    public String toString() {
        return LengthUtils.toString(getObject());
    }
}
