/**
 *
 */
package org.ak2.gui.models.tree;

/**
 * @author Whippet
 *
 */
public interface ITreeTransactionListener
{
    public void start(AbstractTreeModel model);

    public void finish(AbstractTreeModel model);
}
