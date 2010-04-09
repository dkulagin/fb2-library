package org.ak2.gui.controls.tree;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.gui.models.tree.ITreeTransactionListener;
import org.ak2.gui.models.tree.TreeState;
import org.ak2.utils.LengthUtils;

/**
 * @author Dmitriy Kondratenko
 */
public class TreeEx extends JTree implements ITreeTransactionListener
{
    private static final long serialVersionUID = -8976492472473182491L;

    private AbstractTreeModel m_original;

    private Object[] m_lastSelectedPath;

    private final TreeListener m_treeListener = new TreeListener();

    private final FilterFieldListener m_fieldListener = new FilterFieldListener();

    private final AtomicInteger m_transactionCounter = new AtomicInteger(0);

    private final AtomicReference<TreeState> m_transactionState = new AtomicReference<TreeState>(null);

    /**
     * Constructor
     */
    public TreeEx()
    {
        super((TreeModel) null);
        addKeyListener(m_treeListener);
        addTreeSelectionListener(m_treeListener);
    }

    /**
     * This method expand all rows
     */
    public void expandAll()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                for(int row = 0; row < getRowCount(); row++)
                {
                    expandPath(getPathForRow(row));
                }
            }
        });
    }

    /**
     * This method collapse all rows
     */
    public void collapsAll()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                for(int i = 0; i < getRowCount(); i++)
                {
                    collapsePath(getPathForRow(i));
                }
            }
        });
    }

    /**
     * @param text text of filter
     */
    public void filter(final String text)
    {
        final TreeState state = m_transactionState.get() == null ? new TreeState(this) : null;

        final AbstractTreeModel original = getOriginalModel();

        AbstractTreeModel actualModel = original;
        if (LengthUtils.isNotEmpty(text))
        {
            actualModel = original.filter(text);
        }

        setTreeModel(actualModel);
        firePropertyChange("actualModel", null, actualModel);

        if (state != null)
        {
            state.restore(this, true);
        }
    }

    /**
     * This method sets the model of tree
     *
     * @param newModel model of tree
     */
    @Override
    public void setModel(final TreeModel newModel)
    {
        m_original = (AbstractTreeModel) newModel;
        setTreeModel(newModel);
    }

    /**
     * This method sets the model of tree
     *
     * @param newModel model of tree
     */
    protected void setTreeModel(final TreeModel newModel)
    {
        TreeModel oldModel = (AbstractTreeModel) getModel();
        if (oldModel instanceof AbstractTreeModel)
        {
            ((AbstractTreeModel) oldModel).removeTreeTransactionListener(this);
        }
        super.setModel(newModel);

        if (newModel instanceof AbstractTreeModel)
        {
            ((AbstractTreeModel) newModel).addTreeTransactionListener(this);
        }
    }

    /**
     * Gets original model of tree
     *
     * @return m_original original model of tree
     */
    public AbstractTreeModel getOriginalModel()
    {
        if (m_original == null)
        {
            m_original = (AbstractTreeModel) getModel();
        }
        return m_original;
    }

    /**
     * @param model model of tree
     * @param expectedUserObject user object
     * @return node or null
     */
    public AbstractTreeNode<?> getNode(final TreeModel model, final Object expectedUserObject)
    {
        final AbstractTreeNode<?> root = (AbstractTreeNode<?>) model.getRoot();
        for(final Enumeration<AbstractTreeNode<?>> e = root.depthFirstEnumeration(); e.hasMoreElements();)
        {
            final AbstractTreeNode<?> node = e.nextElement();
            if (LengthUtils.equals(node.getObject(), expectedUserObject))
            {
                return node;
            }
        }
        return null;
    }

    /**
     * Retrieves an appropriate tree node.
     *
     * @param model tree model
     * @param expectedNode original node
     * @return an instance of the {@link AbstractTreeNode} or
     *         <code>null</code>
     */
    public AbstractTreeNode<?> getNode(final TreeModel model, final AbstractTreeNode<?> expectedNode)
    {
        final AbstractTreeNode<?> root = (AbstractTreeNode<?>) model.getRoot();
        for(final Enumeration<AbstractTreeNode<?>> e = root.depthFirstEnumeration(); e.hasMoreElements();)
        {
            final AbstractTreeNode<?> node = e.nextElement();
            if (LengthUtils.equals(node, expectedNode))
            {
                return node;
            }
        }
        return null;
    }

    /**
     * @return the selected node
     */
    public AbstractTreeNode<?> getSelectedNode()
    {
        final TreePath selection = getSelectionPath();
        if (selection != null)
        {
            final Object lastNode = selection.getLastPathComponent();
            if (lastNode instanceof AbstractTreeNode)
            {
                return (AbstractTreeNode<?>) lastNode;
            }
        }
        return null;
    }

    /**
     * Selects a node containing the given user object
     *
     * @param nodeToSelect tree node to select
     */
    public void setSelectedNode(final AbstractTreeNode<?> nodeToSelect)
    {
        final AbstractTreeNode<?> node = getNode(getModel(), nodeToSelect);
        if (node != null)
        {
            setSelectionPath(new TreePath(node.getPath()));
        }
    }

    /**
     * This method gets user object of selection node
     *
     * @return getUserObject() or null
     */
    public Object getSelectedUserObject()
    {
        final AbstractTreeNode<?> selected = getSelectedNode();
        return selected != null ? selected.getObject() : null;
    }

    /**
     * @return last selected path containing node user objects
     */
    public Object[] getLastSelectedObjectPath()
    {
        return m_lastSelectedPath;
    }

    /**
     * Selects a node containing the given user object
     *
     * @param userObject user object to select
     */
    public void setSelectedUserObject(final Object userObject)
    {
        final AbstractTreeNode<?> node = getNode(getModel(), userObject);
        if (node != null)
        {
            setSelectionPath(new TreePath(node.getPath()));
        }
    }

    /**
     * Retrieves node showing in the given row.
     *
     * @param row tree row
     * @return an instance of the {@link AbstractTreeNode} or
     *         <code>null</code>
     */
    public AbstractTreeNode<?> getNodeForRow(final int row)
    {
        final TreePath pathForRow = getPathForRow(row);
        if (pathForRow != null)
        {
            return (AbstractTreeNode<?>) pathForRow.getLastPathComponent();
        }
        return null;
    }

    /**
     * Starts tree changes.
     */
    public void startTransaction()
    {
        final TreeModel model = getModel();
        if (model instanceof AbstractTreeModel)
        {
            this.start((AbstractTreeModel) model);
        }
    }

    /**
     * Starts tree changes.
     *
     * @param model tree model
     * @see ITreeTransactionListener#start(AbstractTreeModel)
     */
    public void start(final AbstractTreeModel model)
    {
        if (m_transactionCounter.incrementAndGet() == 1)
        {
            m_transactionState.compareAndSet(null, new TreeState(this));
        }
    }

    /**
     * Finish tree changes.
     */
    public void finishTransaction()
    {
        final TreeModel model = getModel();
        if (model instanceof AbstractTreeModel)
        {
            this.finish((AbstractTreeModel) model);
        }
    }

    /**
     * Finish tree changes.
     *
     * @param model tree model
     * @see ITreeTransactionListener#finish(AbstractTreeModel)
     */
    public void finish(final AbstractTreeModel model)
    {
        if (m_transactionCounter.decrementAndGet() == 0)
        {
            TreeState state = m_transactionState.getAndSet(null);
            if (state != null)
            {
                state.restore(this);
            }
        }
    }

    /**
     * Class TreeListener
     */
    public final class TreeListener extends KeyAdapter implements TreeSelectionListener
    {
        /**
         * Called whenever the value of the selection changes.
         *
         * @param e the event that characterizes the change.
         * @see TreeSelectionListener#valueChanged(TreeSelectionEvent)
         */
        public void valueChanged(final TreeSelectionEvent e)
        {
            final AbstractTreeNode<?> selectedNode = getSelectedNode();
            if (selectedNode != null)
            {
                m_lastSelectedPath = selectedNode.getUserObjectPath();
            }
        }

        /**
         * @param e key event
         */
        @Override
        public void keyReleased(final KeyEvent e)
        {
//            final FilterField filterField = getFilterField();
//            if (filterField != null)
//            {
//                final char keyChar = e.getKeyChar();
//                if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar >= ' ')
//                {
//                    final StringBuilder buf = new StringBuilder(filterField.getText());
//                    buf.append(keyChar);
//                    filterField.setText(buf.toString());
//                }
//                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
//                {
//                    final StringBuilder buf = new StringBuilder(filterField.getText());
//                    buf.setLength(Math.max(0, buf.length() - 1));
//                    filterField.setText(buf.toString());
//                }
//            }
        }
    }

    /**
     * Class FilterFieldListener Contains methods for different listeners
     */
    private final class FilterFieldListener implements PropertyChangeListener
    {
        /**
         * This method gets called when a bound property is changed.
         *
         * @param evt A PropertyChangeEvent object describing the event
         *            source and the property that has changed.
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(final PropertyChangeEvent evt)
        {
//            filter(getFilterField().getText());
        }

    }
}
