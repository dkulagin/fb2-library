package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.utils.LengthUtils;

public class AuthorFilterNode extends AbstractFilterNode<BookAuthor> {

    private boolean includeSequenced;

    public AuthorFilterNode(AbstractTreeModel model, BookAuthor author, boolean includeSequenced) {
        super(model, author);
        this.includeSequenced = includeSequenced;
    }

    @Override
    protected boolean accept(BookInfo entity, BookAuthor author) {
        return author.equals(entity.getAuthor()) && (includeSequenced || LengthUtils.isEmpty(entity.getSequence()));
    }
}