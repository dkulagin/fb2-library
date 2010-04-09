/**
 *
 */
package org.ak2.gui.models.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * @author Alexander Kasatkin
 */
public abstract class AbstractTreeModel extends DefaultTreeModel
{
    private static final long serialVersionUID = 6562904493037953639L;

    private final List<ITreeTransactionListener> m_listeners = new ArrayList<ITreeTransactionListener>();

    private AbstractTreeNode<?> m_rootNode;

    /**
     * Constructor
     */
    protected AbstractTreeModel()
    {
        super(null);
    }

    /**
     * Constructor.
     *
     * @param asksAllowsChildren a boolean, false if any node can have
     *            children, true if each node is asked to see if it can have
     *            children
     */
    protected AbstractTreeModel(final boolean asksAllowsChildren)
    {
        super(null, asksAllowsChildren);
    }

    /**
     * Returns the root of the tree. Returns null only if the tree has no
     * nodes.
     *
     * @return the root of the tree
     * @see javax.swing.tree.DefaultTreeModel#getRoot()
     */
    @Override
    @Deprecated
    public Object getRoot()
    {
        return getRootNode();
    }

    /**
     * Prevents to set another tree root node
     *
     * @param root tree root node
     * @see javax.swing.tree.DefaultTreeModel#setRoot(javax.swing.tree.TreeNode)
     */
    @Override
    @Deprecated
    @SuppressWarnings("unchecked")
    public void setRoot(final TreeNode root)
    {
        setRootNode((AbstractTreeNode<?>) root);
    }

    /**
     * Retrieves a root node.
     *
     * @param <UserObjectType> expected type of a root user object
     * @return an instance of the {@link AbstractTreeNode} object
     */
    @SuppressWarnings("unchecked")
    public <UserObjectType> AbstractTreeNode<UserObjectType> getRootNode()
    {
        return (AbstractTreeNode<UserObjectType>) m_rootNode;
    }

    /**
     * Sets a root node.
     *
     * @param newRoot new root to set
     */
    public void setRootNode(final AbstractTreeNode<?> newRoot)
    {
        m_rootNode = newRoot;
    }

    /**
     * Fires changes of the given node to a tree.
     *
     * @param parent changed node
     */
    public void fireNodeChanged(final AbstractTreeNode<?> parent)
    {
        nodeStructureChanged(parent);
    }

    /**
     * Fires insertion info to a tree.
     *
     * @param parent parent node
     * @param child inserted child
     */
    public void fireNodeInserted(final AbstractTreeNode<?> parent, final AbstractTreeNode<?> child)
    {
        final int index = parent.getIndex(child);
        fireTreeNodesInserted(this, parent.getPath(), new int[] { index }, new Object[] { child });
    }

    /**
     * Fires node changes to a tree.
     *
     * @param parent parent node
     * @param child changed node
     */
    public void fireNodeChanged(final AbstractTreeNode<?> parent, final AbstractTreeNode<?> child)
    {
        final int index = parent.getIndex(child);
        fireTreeNodesChanged(this, parent.getPath(), new int[] { index }, new Object[] { child });
    }

    /**
     * Fires removing info to a tree.
     *
     * @param parent parent node
     * @param index removed node
     * @param node old index of removed node
     */
    public void fireNodeRemoved(final AbstractTreeNode<?> parent, final int index, final AbstractTreeNode<?> node)
    {
        fireTreeNodesRemoved(this, parent.getPath(), new int[] { index }, new Object[] { node });
    }

    /**
     * Fires removing info to a tree.
     *
     * @param parent parent node
     * @param nodes removed nodes
     */
    public void fireAllNodesRemoved(final AbstractTreeNode<?> parent, final Object[] nodes)
    {
        final int[] indexes = new int[nodes.length];
        for(int i = 0; i < indexes.length; i++)
        {
            indexes[i] = i;
        }
        fireTreeNodesRemoved(this, parent.getPath(), indexes, nodes);
    }

    /**
     * Adds a transaction listener.
     *
     * @param listener listener to add
     */
    public final void addTreeTransactionListener(final ITreeTransactionListener listener)
    {
        if (!m_listeners.contains(listener))
        {
            m_listeners.add(listener);
        }
    }

    /**
     * Removes a transaction listener.
     *
     * @param listener listener to remove
     */
    public final void removeTreeTransactionListener(final ITreeTransactionListener listener)
    {
        m_listeners.remove(listener);
    }

    /**
     * Calls the {@link ITreeTransactionListener#start(AbstractTreeModel)}
     * callback for all registered listeners.
     */
    public final void fireTransactionStarted()
    {
        for(ITreeTransactionListener treeTransactionListener : m_listeners)
        {
            treeTransactionListener.start(this);
        }
    }

    /**
     * Calls the {@link ITreeTransactionListener#finish(AbstractTreeModel)}
     * callback for all registered listeners.
     */
    public final void fireTransactionFinished()
    {
        for(ITreeTransactionListener treeTransactionListener : m_listeners)
        {
            treeTransactionListener.finish(this);
        }
    }

    /**
     * Creates new filtered model for the given text
     * @param text search text
     * @return an instance of the {@link SearchTreeModel} object
     */
    public final SearchTreeModel filter(final String text)
    {
        final SearchTreeModel model = createSearchModel();
        model.filter(this, text);
        return model;
    }

    /**
     * Creates a search model.
     *
     * @return an instance of the {@link SearchTreeModel} object
     */
    protected SearchTreeModel createSearchModel()
    {
        return new SearchTreeModel();
    }
}
