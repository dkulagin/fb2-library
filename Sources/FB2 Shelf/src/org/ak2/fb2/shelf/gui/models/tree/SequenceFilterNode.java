package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.utils.LengthUtils;

public class SequenceFilterNode extends AbstractFilterNode<String> {

    public SequenceFilterNode(AbstractTreeModel model, String sequence) {
        super(model, LengthUtils.safeString(sequence));
    }

    @Override
    protected boolean accept(BookInfo entity, String sequence) {
        String seq = entity.getSequence();
        if (seq == null) {
            return false;
        }
        return sequence.equals(seq);
    }

    @Override
    public String toString() {
        return LengthUtils.safeString(getObject(), "Unknown");
    }

}
