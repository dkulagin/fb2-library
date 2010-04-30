package org.ak2.fb2.shelf.gui.models.tree;

import java.util.ArrayList;
import java.util.Collection;
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

    public ShelfFilterModel(final ShelfCatalog catalog) {
        this.catalog = catalog;

        final RootFilterNode root = new RootFilterNode(this, catalog);
        this.setRootNode(root);

        final Collection<BookAuthor> authors = catalog.getAuthors();

        final int size = 20;
        if (LengthUtils.length(authors) < size) {
            addAuthors(root, authors);
        } else {
            final List<BookAuthor> nodeAuthors = new ArrayList<BookAuthor>();
            char prevChar = '&';
            for (final BookAuthor bookAuthor : authors) {
                final char firstChar = getFirstChar(bookAuthor.getName());
                if (prevChar != firstChar && nodeAuthors.size() > 0 || nodeAuthors.size() >= size) {
                    final AuthorPackFilterNode packNode = new AuthorPackFilterNode(this, nodeAuthors);
                    root.add(packNode);
                    addAuthors(packNode, nodeAuthors);
                    nodeAuthors.clear();
                }
                prevChar = firstChar;
                nodeAuthors.add(bookAuthor);
            }
            if (nodeAuthors.size() > 0) {
                final AuthorPackFilterNode packNode = new AuthorPackFilterNode(this, nodeAuthors);
                root.add(packNode);
                addAuthors(packNode, nodeAuthors);
                nodeAuthors.clear();
            }
        }
    }

    private char getFirstChar(final String str) {
        char firstChar = LengthUtils.safeString(str, " ").charAt(0);
        if (Character.isLetter(firstChar)) {
            firstChar = Character.toLowerCase(firstChar);
        } else if (Character.isDigit(firstChar)) {
            firstChar = '0';
        } else {
            firstChar = ' ';
        }
        return firstChar;
    }

    private void addAuthors(final AbstractTreeNode<?> parent, final Collection<BookAuthor> packAuthors) {
        for (final BookAuthor bookAuthor : packAuthors) {
            final List<BookInfo> books = catalog.getBooks(bookAuthor);
            final AuthorFilterNode authorNode = new AuthorFilterNode(this, bookAuthor, books, false);
            parent.add(authorNode);

            final Set<String> set = catalog.getSequences(bookAuthor);
            if (LengthUtils.isNotEmpty(set)) {
                for (final String sequence : set) {
                    final AuthorSequenceFilterNode seqNode = new AuthorSequenceFilterNode(this, bookAuthor, sequence, books);
                    authorNode.add(seqNode);
                }
            }
        }
    }

    public ShelfCatalog getCatalog() {
        return catalog;
    }
}
