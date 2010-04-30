package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.tree.AbstractTreeModel;

public class RootFilterNode extends AbstractBooksNode<String> {

    public RootFilterNode(AbstractTreeModel model, ShelfCatalog catalog) {
        super(model, "ALL", catalog.getBooks());
    }
}
