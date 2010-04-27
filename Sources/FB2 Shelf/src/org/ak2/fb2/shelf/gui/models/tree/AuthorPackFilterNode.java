package org.ak2.fb2.shelf.gui.models.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.utils.LengthUtils;

public class AuthorPackFilterNode extends AbstractFilterNode<String> {

    private final LinkedList<BookAuthor> authors;

    public AuthorPackFilterNode(final AbstractTreeModel model, final Iterator<BookAuthor> authors, final int size) {
        super(model, "", null);

        this.authors = new LinkedList<BookAuthor>();
        for (int i = 0; i < size && authors.hasNext(); i++) {
            final BookAuthor author = authors.next();
            this.authors.add(author);
        }

        final BookAuthor first = this.authors.getFirst();
        if (this.authors.size() > 1) {
            final BookAuthor last = this.authors.getLast();
            this.setObject(first.getName() + " - " + last.getName());
        } else {
            this.setObject(first.getName());
        }
    }

    @Override
    protected boolean accept(final BookInfo entity, final String userObject) {
        final BookAuthor first = this.authors.getFirst();
        final BookAuthor last = this.authors.getLast();
        final BookAuthor author = entity.getAuthor();

        if (author.before(first)) {
            return false;
        }
        if (author.after(last)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return LengthUtils.toString(getObject());
    }

    public List<BookAuthor> getAuthors() {
        return authors;
    }
}
