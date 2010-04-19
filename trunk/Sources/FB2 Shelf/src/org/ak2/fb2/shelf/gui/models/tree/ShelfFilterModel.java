package org.ak2.fb2.shelf.gui.models.tree;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.utils.LengthUtils;

public class ShelfFilterModel extends AbstractTreeModel {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3597351806379855067L;

    private final ShelfCatalog catalog;

    public ShelfFilterModel(ShelfCatalog catalog) {
        this.catalog = catalog;

        Map<BookAuthor, Set<String>> authors = new LinkedHashMap<BookAuthor, Set<String>>();
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

        for (Map.Entry<BookAuthor, Set<String>> author : authors.entrySet()) {
            AuthorFilterNode node = new AuthorFilterNode(this, author.getKey());
            Set<String> set = author.getValue();
            if (LengthUtils.isNotEmpty(set)) {
                for (String sequence : set) {
                    SequenceFilterNode seqNode = new SequenceFilterNode(this, sequence);
                    node.add(seqNode);
                }
            }
            root.add(node);
        }
    }

    public ShelfCatalog getCatalog() {
        return catalog;
    }
}
