package org.ak2.fb2.shelf.gui.models.tree;

import java.util.List;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.SearchTreeModel;
import org.ak2.utils.LengthUtils;

public class SequenceFilterNode extends AbstractBooksNode<String> {

    private final String shortName;

    public SequenceFilterNode(final AbstractTreeModel model, final String fullName, final String shortName, final List<BookInfo> books) {
        super(model, fullName, books);
        this.shortName = shortName;
    }

    public String getFullName() {
        return getObject();
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public String toString() {
        return LengthUtils.safeString(getShortName(), getFullName());
    }

    @Override
    public boolean containsText(final String expected) {
        return SearchTreeModel.containsText(getFullName(), expected);
    }
}
