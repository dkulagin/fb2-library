package org.ak2.fb2.shelf.gui.models.tree;

import java.util.List;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.SearchTreeModel;
import org.ak2.utils.LengthUtils;

public class AuthorSequenceFilterNode extends AbstractFilterNode<String> {

    private final BookAuthor author;

    public AuthorSequenceFilterNode(final AbstractTreeModel model, final BookAuthor author, final String sequence, final List<BookInfo> books) {
        super(model, LengthUtils.safeString(sequence), books);
        this.author = author;
    }

    @Override
    protected boolean accept(final BookInfo entity, final String sequence) {
        if (this.author != null) {
            if (!author.equals(entity.getAuthor())) {
                return false;
            }
        }
        final String seq = entity.getSequence();
        if (seq == null) {
            return false;
        }
        return sequence.equals(seq);
    }

    @Override
    public boolean containsText(final String expected) {
        if (super.containsText(expected)) {
            return true;
        }
        return SearchTreeModel.containsText(author.getName(), expected);
    }
}
