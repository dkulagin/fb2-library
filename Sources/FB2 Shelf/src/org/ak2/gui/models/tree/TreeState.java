package org.ak2.gui.models.tree;

import java.util.HashSet;

import org.ak2.gui.controls.tree.TreeEx;
import org.ak2.utils.LengthUtils;

public class TreeState {

    private final HashSet<AbstractTreeNode<?>> m_expanded = new HashSet<AbstractTreeNode<?>>();

    private Object[] m_selected;

    /**
     * Constructor
     */
    public TreeState(TreeEx tree) {
        final AbstractTreeNode<?> selected = tree.getSelectedNode();
        m_selected = selected != null ? selected.getUserObjectPath() : null;

        int count = tree.getRowCount();
        for (int i = 0; i < count; i++) {
            if (tree.isExpanded(i)) {
                AbstractTreeNode<?> node = tree.getNodeForRow(i);
                m_expanded.add(node);
            }
        }
    }

    /**
     * Restores states
     */
    public void restore(final TreeEx tree) {
        restore(tree, false);
    }

    /**
     * Restores states
     */
    public void restore(final TreeEx tree, boolean expandAll) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);
        }
        if (!expandAll) {
            for (int row = tree.getRowCount() - 1; row >= 0; row--) {
                AbstractTreeNode<?> node = tree.getNodeForRow(row);
                if (!m_expanded.contains(node)) {
                    tree.collapseRow(row);
                }
            }
        }

        AbstractTreeNode<?> nodeToSelect = checkPath(tree, m_selected);
        if (nodeToSelect == null) {
            nodeToSelect = checkPath(tree, tree.getLastSelectedObjectPath());
        }

        tree.setSelectedNode(nodeToSelect);

        m_expanded.clear();
        m_selected = null;
    }

    /**
     * Restores states
     */
    public void restore(final TreeEx tree, AbstractTreeNode<?> target, boolean expandTarget) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);
        }
        for (int row = tree.getRowCount() - 1; row >= 0; row--) {
            AbstractTreeNode<?> node = tree.getNodeForRow(row);
            if (!m_expanded.contains(node)) {
                tree.collapseRow(row);
            }
        }

        AbstractTreeNode<?> nodeToSelect = checkPath(tree, target != null ? target.getUserObjectPath() : m_selected);
        if (nodeToSelect == null) {
            nodeToSelect = checkPath(tree, tree.getLastSelectedObjectPath());
        }

        if (expandTarget && nodeToSelect != null) {
            tree.expandPath(nodeToSelect.getTreePath());
        }
        tree.setSelectedNode(nodeToSelect);

        m_expanded.clear();
        m_selected = null;
    }

    /**
     * @param tree
     */
    private AbstractTreeNode<?> checkPath(final TreeEx tree, final Object[] selected) {
        if (LengthUtils.isNotEmpty(selected)) {
            for (int i = selected.length - 1; i >= 0; i--) {
                AbstractTreeNode<?> node = tree.getNode(tree.getModel(), selected[i]);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }
}
