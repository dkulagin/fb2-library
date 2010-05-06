package org.ak2.fb2.shelf.gui.models.tree;

import java.util.List;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.SearchTreeModel;
import org.ak2.utils.LengthUtils;

public class SequenceFilterNode extends AbstractBooksNode<String> {

    private final String fullName;

    public SequenceFilterNode(final AbstractTreeModel model, final String fullName, final String shortName, final List<BookInfo> books) {
        super(model, LengthUtils.safeString(shortName, fullName), books);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean containsText(final String expected) {
        return SearchTreeModel.containsText(fullName, expected);
    }
}
