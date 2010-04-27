package org.ak2.fb2.shelf.gui.models.tree;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.gui.models.catalog.ShelfCatalogModel;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.table.impl.CompositeTableModel;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.refs.IRef;
import org.ak2.utils.refs.ReferenceUtils;
import org.ak2.utils.refs.WeakRef;

public abstract class AbstractFilterNode<T> extends AbstractTreeNode<T> implements IEntityFilter<BookInfo> {

    protected final List<BookInfo> m_books;

    protected IRef<ITableModel<BookInfo, ?>> m_booksModel;

    protected AbstractFilterNode(final AbstractTreeModel model, final T userObject, List<BookInfo> books) {
        super(model, userObject);
        m_books = books;
    }

    @SuppressWarnings("unchecked")
    public final ITableModel<BookInfo, ?> getBooksModel() {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalThreadStateException("This method cannot be called from Swing event thread");
        }

        ITableModel<BookInfo, ?> model = ReferenceUtils.get(m_booksModel);
        if (model == null) {
            model = createBooksModel();
            if (m_books != null) {
                IEntityFilter<BookInfo> filter = this;
                model.setFilter(filter);
            }
            m_booksModel = new WeakRef<ITableModel<BookInfo, ?>>(model);
        }
        return model;
    }

    protected final ITableModel<BookInfo, ?> createBooksModel() {
        if (m_books != null) {
            return new ShelfCatalogModel(m_books);
        }

        List<ITableModel<BookInfo, ?>> models = new LinkedList<ITableModel<BookInfo, ?>>();
        Enumeration<AbstractTreeNode<?>> en = this.getChildren();
        while (en.hasMoreElements()) {
            AbstractFilterNode<?> child = (AbstractFilterNode<?>) en.nextElement();
            ITableModel<BookInfo, ?> booksModel = child.createBooksModel();
            models.add(booksModel);
        }
        CompositeTableModel<BookInfo> ctm = new CompositeTableModel<BookInfo>(ShelfCatalogModel.COLUMNS, ShelfCatalogModel.ADAPTERS);
        ctm.setData(models);
        return ctm;
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
