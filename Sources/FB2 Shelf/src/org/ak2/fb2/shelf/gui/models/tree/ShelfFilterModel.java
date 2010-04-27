package org.ak2.fb2.shelf.gui.models.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.utils.LengthUtils;

public class ShelfFilterModel extends AbstractTreeModel {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3597351806379855067L;

    private final ShelfCatalog catalog;

    public ShelfFilterModel(ShelfCatalog catalog) {
        this.catalog = catalog;

        RootFilterNode root = new RootFilterNode(this, catalog);
        this.setRootNode(root);

        Collection<BookAuthor> authors = catalog.getAuthors();

        int size = 20;
        if (LengthUtils.length(authors) < size) {
            addAuthors((AbstractTreeNode<?>) root, authors);
        } else {
            Iterator<BookAuthor> iter = authors.iterator();
            while (iter.hasNext()) {
                AuthorPackFilterNode packNode = new AuthorPackFilterNode(this, iter, size);
                root.add(packNode);
                addAuthors((AbstractTreeNode<?>) packNode, packNode.getAuthors());
            }
        }
    }

    private void addAuthors(AbstractTreeNode<?> parent, Collection<BookAuthor> packAuthors) {
        for (BookAuthor bookAuthor : packAuthors) {
            List<BookInfo> books = catalog.getBooks(bookAuthor);
            AuthorFilterNode authorNode = new AuthorFilterNode(this, bookAuthor, books, false);
            parent.add(authorNode);

            Set<String> set = catalog.getSequences(bookAuthor);
            if (LengthUtils.isNotEmpty(set)) {
                for (String sequence : set) {
                    AuthorSequenceFilterNode seqNode = new AuthorSequenceFilterNode(this, bookAuthor, sequence, books);
                    authorNode.add(seqNode);
                }
            }
        }
    }

    public ShelfCatalog getCatalog() {
        return catalog;
    }
}
