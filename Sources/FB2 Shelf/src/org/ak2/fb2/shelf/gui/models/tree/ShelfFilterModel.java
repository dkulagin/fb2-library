package org.ak2.fb2.shelf.gui.models.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

        Map<BookAuthor, Set<String>> authors = new TreeMap<BookAuthor, Set<String>>();
        for (BookInfo book : catalog) {
            BookAuthor author = book.getAuthor();
            Set<String> set = authors.get(author);
            if (set == null) {
                set = new TreeSet<String>();
                authors.put(author, set);
            }
            String sequence = book.getSequence();
            if (LengthUtils.isNotEmpty(sequence)) {
                set.add(sequence);
            }
        }

        RootFilterNode root = new RootFilterNode(this);
        this.setRootNode(root);

        int size = 20;
        if (authors.size() < size) {
            addAuthors((AbstractTreeNode<?>) root, authors.keySet(), authors);
        } else {
            Iterator<BookAuthor> iter = authors.keySet().iterator();
            while (iter.hasNext()) {
                AuthorPackFilterNode packNode = new AuthorPackFilterNode(this, iter, size);
                root.add(packNode);
                addAuthors((AbstractTreeNode<?>) packNode, packNode.getAuthors(), authors);
            }
        }
    }

    private void addAuthors(AbstractTreeNode<?> parent, Collection<BookAuthor> packAuthors, Map<BookAuthor, Set<String>> authors) {
        for (BookAuthor bookAuthor : packAuthors) {
            AuthorFilterNode authorNode = new AuthorFilterNode(this, bookAuthor, false);
            parent.add(authorNode);

            Set<String> set = authors.get(bookAuthor);
            if (LengthUtils.isNotEmpty(set)) {
                for (String sequence : set) {
                    AuthorSequenceFilterNode seqNode = new AuthorSequenceFilterNode(this, bookAuthor, sequence);
                    authorNode.add(seqNode);
                }
            }
        }
    }

    public ShelfCatalog getCatalog() {
        return catalog;
    }
}
