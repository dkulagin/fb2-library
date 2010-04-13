package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.AbstractTreeNode;

public class AuthorFilterNode extends AbstractTreeNode<BookAuthor> implements IEntityFilter<BookInfo> {

    public AuthorFilterNode(AbstractTreeModel model, BookAuthor author) {
        super(model, author);
    }

    @Override
    public boolean accept(BookInfo entity) {
        BookAuthor object = getObject();
        if (object == null) {
            return true;
        }
        return object.equals(entity.getAuthor());
    }

    @Override
    public String toString() {
        BookAuthor object = getObject();
        return object == null ? "ALL" : object.toString();
    }

}
