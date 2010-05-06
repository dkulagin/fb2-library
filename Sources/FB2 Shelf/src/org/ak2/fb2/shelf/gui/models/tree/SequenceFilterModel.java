package org.ak2.fb2.shelf.gui.models.tree;

import java.util.Collection;
import java.util.List;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.utils.LengthUtils;

public class SequenceFilterModel extends AbstractTreeModel {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3597351806379855067L;

    private final ShelfCatalog catalog;

    public SequenceFilterModel(final ShelfCatalog catalog) {
        this.catalog = catalog;

        final RootFilterNode root = new RootFilterNode(this, catalog);
        this.setRootNode(root);

        final Collection<String> sequences = catalog.getSequences();
        SequenceFilterNode node = null;
        for (final String sequence : sequences) {
            final List<BookInfo> books = catalog.getBooks(sequence);
            if (node != null) {
                final String sub = getSubsequence(node.getFullName(), sequence);
                if (sub != null) {
                    node.add(new SequenceFilterNode(this, sequence, sub, books));
                    continue;
                }
            }
            node = new SequenceFilterNode(this, sequence, null, books);
            root.add(node);
        }
    }

    protected String getSubsequence(final String prevSequence, final String sequence) {
        if (sequence.startsWith(prevSequence)) {
            final String suffix = sequence.substring(prevSequence.length()).trim();
            if (LengthUtils.isNotEmpty(suffix)) {
                switch (suffix.charAt(0)) {
                case ':':
                case '.':
                    return suffix.substring(1).trim();
                default:
                    return null;
                }
            }
        }
        return null;
    }

    public ShelfCatalog getCatalog() {
        return catalog;
    }
}
