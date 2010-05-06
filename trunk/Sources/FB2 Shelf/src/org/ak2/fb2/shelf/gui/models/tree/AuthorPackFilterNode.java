package org.ak2.fb2.shelf.gui.models.tree;

import java.util.Collection;
import java.util.LinkedList;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.gui.models.tree.AbstractTreeModel;

public class AuthorPackFilterNode extends AbstractBooksNode<String> {

    private final LinkedList<BookAuthor> authors;

    public AuthorPackFilterNode(final AbstractTreeModel model, final Collection<BookAuthor> authors) {
        super(model, "", null);

        this.authors = new LinkedList<BookAuthor>(authors);
        final BookAuthor first = this.authors.getFirst();
        if (this.authors.size() > 1) {
            final BookAuthor last = this.authors.getLast();
            this.setObject(first.getName() + " - " + last.getName());
        } else {
            this.setObject(first.getName());
        }
    }

    @Override
    public boolean containsText(String expected) {
        return false;
    }
}
