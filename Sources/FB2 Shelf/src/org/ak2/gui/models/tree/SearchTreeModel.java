package org.ak2.gui.models.tree;

import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;

import org.ak2.utils.LengthUtils;

public class SearchTreeModel extends AbstractTreeModel {
    private static final long serialVersionUID = 8078565145638245032L;

    private final HashMap<AbstractTreeNode<?>, AbstractTreeNode<?>> m_map = new HashMap<AbstractTreeNode<?>, AbstractTreeNode<?>>();

    private String m_lastText;

    /**
     * Constructor.
     */
    protected SearchTreeModel() {
        super();
    }

    /**
     * This method filtering text
     *
     * @param treeModel
     *            model of tree
     * @param text
     *            current text
     * @return this
     */
    public AbstractTreeModel filter(final AbstractTreeModel treeModel, final String text) {
        m_lastText = text;

        final AbstractTreeNode<?> root = treeModel.getRootNode();
        final AbstractTreeNode<?> newRoot = createRoot(root);

        for (final Enumeration<AbstractTreeNode<?>> en = root.preorderEnumeration(); en.hasMoreElements();) {
            final AbstractTreeNode<?> node = (AbstractTreeNode<?>) en.nextElement();
            if (accept(node, text)) {
                addNode(node, newRoot);
            }
        }

        final AbstractTreeNode<?> oldRoot = this.getRootNode();
        this.setRootNode(newRoot);

        m_map.clear();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (oldRoot != null) {
                    oldRoot.release();
                }
            }
        });

        return this;
    }

    /**
     * Fires node changes to a tree
     *
     * @param parent
     *            parent node
     * @param child
     *            changed node
     */
    @Override
    public void fireNodeChanged(final AbstractTreeNode<?> parent, final AbstractTreeNode<?> child) {
        final String lastText = getLastText();
        if (LengthUtils.isNotEmpty(lastText) && !accept(child, lastText)) {
            this.fireTransactionStarted();
            this.removeNodeFromParent(child);
            this.fireTransactionFinished();
            return;
        }
        super.fireNodeChanged(parent, child);
    }

    /**
     * Invoke this method after you've removed some TreeNodes from node.
     *
     * @param node
     *            parent node
     * @param childIndices
     *            indexes of the removed elements
     * @param removedChildren
     *            array of the children objects that were removed.
     * @see javax.swing.tree.DefaultTreeModel#nodesWereRemoved(javax.swing.tree.TreeNode, int[], java.lang.Object[])
     */
    @Override
    public void nodesWereRemoved(final TreeNode node, final int[] childIndices, final Object[] removedChildren) {
        try {
            super.nodesWereRemoved(node, childIndices, removedChildren);
            final AbstractTreeNode<?> parent = (AbstractTreeNode<?>) node;
            final String lastText = getLastText();

            if (LengthUtils.isNotEmpty(lastText) && !checkParent(parent, lastText)) {
                if (parent.getParentNode() != null) {
                    this.removeNodeFromParent(parent);
                }
            }
        } catch (Throwable th) {
        }
    }

    /**
     * Check if the parent node or one of its children accept the filter condition.
     *
     * @param parent
     *            parent node
     * @param text
     *            filter text
     * @return <code>true</code> if the parent node or one of its children accept the filter condition.
     */
    protected boolean checkParent(final AbstractTreeNode<?> parent, final String text) {
        if (accept(parent, text)) {
            return true;
        }
        for (final Enumeration<?> en = parent.depthFirstEnumeration(); en.hasMoreElements();) {
            final AbstractTreeNode<?> childNode = (AbstractTreeNode<?>) en.nextElement();
            if (accept(childNode, text)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks the given node.
     *
     * @param node
     *            tree node
     * @param text
     *            any text
     * @return accept(nodeName,text)
     */
    protected boolean accept(final AbstractTreeNode<?> node, final String text) {
        return node.containsText(text);
    }

    /**
     * Returns node text to check.
     *
     * @param node
     *            node of tree
     * @return node text
     */
    protected String getNodeText(final AbstractTreeNode<?> node) {
        return node.toString();
    }

    /**
     * @param original
     *            node of tree
     * @param newRoot
     *            new model root
     */
    protected void addNode(final AbstractTreeNode<?> original, final AbstractTreeNode<?> newRoot) {
        final TreeNode[] originalPath = original.getPath();

        AbstractTreeNode<?> parent = newRoot;
        for (int i = 1; i < originalPath.length; i++) {
            final AbstractTreeNode<?> current = (AbstractTreeNode<?>) originalPath[i];
            AbstractTreeNode<?> copy = m_map.get(current);
            if (copy == null) {
                copy = createNode(current);
                m_map.put(current, copy);
                parent.add(copy);
            }
            parent = copy;
        }

        parent.setAccepted(true);
    }

    /**
     * Creates a copy of root node.
     *
     * @param original
     *            original root node
     * @return an instance of the {@link AbstractTreeNode} object
     */
    protected AbstractTreeNode<?> createRoot(final AbstractTreeNode<?> original) {
        return createNode(original);
    }

    /**
     * @param original
     *            node of tree
     * @return new node containing the same user object
     */
    protected AbstractTreeNode<?> createNode(final AbstractTreeNode<?> original) {
        final AbstractTreeNode<?> node = (AbstractTreeNode<?>) original.clone();
        node.setModel(this);
        return node;
    }

    /**
     * @return the lastText
     */
    protected String getLastText() {
        return m_lastText;
    }

    /**
     * Correct string which contains any string.
     *
     * @param string
     *            text
     * @param expected
     *            text
     * @return string
     */
    public static boolean containsText(final String string, final String expected) {
        final String text = LengthUtils.safeString(string).toLowerCase();
        return text.contains(expected.toLowerCase());
    }

}