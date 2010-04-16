package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;

public class AuthorFilterNode extends AbstractFilterNode<BookAuthor> {

    public AuthorFilterNode(AbstractTreeModel model, BookAuthor author) {
        super(model, author);
    }

    @Override
    protected boolean accept(BookInfo entity, BookAuthor author) {
        return author.equals(entity.getAuthor());
    }

    @Override
    public String toString() {
        BookAuthor object = getObject();
        return object == null ? "ALL" : object.toString();
    }

}
