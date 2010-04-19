package org.ak2.fb2.shelf.gui.models.tree;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.gui.models.tree.AbstractTreeModel;

public class AuthorSequenceFilterNode extends SequenceFilterNode {

    private final BookAuthor author;

    public AuthorSequenceFilterNode(AbstractTreeModel model, BookAuthor author, String sequence) {
        super(model, sequence);
        this.author = author;
    }

    @Override
    protected boolean accept(BookInfo entity, String sequence) {
        if (this.author != null) {
            if (!author.equals(entity.getAuthor())) {
                return false;
            }
        }
        String seq = entity.getSequence();
        if (seq == null) {
            return false;
        }
        return sequence.equals(seq);
    }
}
