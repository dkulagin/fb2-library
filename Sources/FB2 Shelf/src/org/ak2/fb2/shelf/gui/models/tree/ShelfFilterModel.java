package org.ak2.fb2.shelf.gui.models.tree;

import java.util.LinkedHashSet;
import java.util.Set;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.tree.AbstractTreeModel;

public class ShelfFilterModel extends AbstractTreeModel {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3597351806379855067L;

    public ShelfFilterModel(ShelfCatalog catalog) {
        Set<BookAuthor> authors = new LinkedHashSet<BookAuthor>();
        for(BookInfo book : catalog) {
            authors.add(book.getAuthor());
        }

        AuthorFilterNode root = new AuthorFilterNode(this, null);
        this.setRootNode(root);

        for(BookAuthor author : authors) {
            AuthorFilterNode node = new AuthorFilterNode(this, author);
            root.add(node);
        }
    }

}
