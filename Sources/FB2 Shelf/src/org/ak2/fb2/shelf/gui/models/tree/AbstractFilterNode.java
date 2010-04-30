package org.ak2.fb2.shelf.gui.models.tree;

import java.util.List;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.tree.AbstractTreeModel;

public abstract class AbstractFilterNode<T> extends AbstractBooksNode<T> implements IEntityFilter<BookInfo> {

    protected AbstractFilterNode(final AbstractTreeModel model, final T userObject, List<BookInfo> books) {
        super(model, userObject, books);
    }

    @SuppressWarnings("unchecked")
    public final ITableModel<BookInfo, ?> getBooksModel() {
        ITableModel<BookInfo, ?> booksModel = super.getBooksModel();
        booksModel.setFilter(this);
        return booksModel;
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
}
