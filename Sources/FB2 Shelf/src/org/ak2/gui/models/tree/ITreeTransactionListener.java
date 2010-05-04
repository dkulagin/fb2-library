package org.ak2.gui.models.tree;

public interface ITreeTransactionListener {
    public void start(AbstractTreeModel model);

    public void finish(AbstractTreeModel model);
}
