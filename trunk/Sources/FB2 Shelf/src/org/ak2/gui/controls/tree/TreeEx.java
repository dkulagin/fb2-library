package org.ak2.gui.controls.tree;

import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ak2.gui.models.tree.AbstractTreeModel;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.gui.models.tree.ITreeTransactionListener;
import org.ak2.gui.models.tree.TreeState;
import org.ak2.utils.LengthUtils;

public class TreeEx extends JTree implements ITreeTransactionListener {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -8976492472473182491L;

    private AbstractTreeModel m_original;

    private Object[] m_lastSelectedPath;

    private final AtomicInteger m_transactionCounter = new AtomicInteger(0);

    private final AtomicReference<TreeState> m_transactionState = new AtomicReference<TreeState>(null);

    private ProxySelectionModel m_selectionModel;

    private final List<ITreeFilterListener> m_filterListeners = new LinkedList<ITreeFilterListener>();

    /**
     * Constructor
     */
    public TreeEx() {
        super((TreeModel) null);
        m_selectionModel = new ProxySelectionModel(getSelectionModel());
        super.setSelectionModel(m_selectionModel);
        addTreeSelectionListener(new TreeListener());
    }

    /**
     * This method expand all rows
     */
    public void expandAll() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startTransaction(false);
                for (int row = 0; row < getRowCount(); row++) {
                    expandPath(getPathForRow(row));
                }
                finishTransaction();
            }
        });
    }

    /**
     * This method collapse all rows
     */
    public void collapsAll() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startTransaction(false);
                for (int i = getRowCount() - 1; i >= 0; i--) {
                    collapsePath(getPathForRow(i));
                }
                finishTransaction();
            }
        });
    }

    @Override
    public void setSelectionModel(final TreeSelectionModel selectionModel) {
        if (m_selectionModel.model != selectionModel) {
            m_selectionModel = new ProxySelectionModel(selectionModel);
            super.setSelectionModel(m_selectionModel);
        }
    }

    /**
     * @param text
     *            text of filter
     */
    public void filter(final String text, final boolean parallel) {
        startTransaction(true);
        if (parallel) {
            final CountDownLatch sem = new CountDownLatch(1);
            final SwingWorker<AbstractTreeModel, String> task = new SwingWorker<AbstractTreeModel, String>() {
                @Override
                protected AbstractTreeModel doInBackground() throws Exception {
                    sem.await();
                    return filterPhase1(text);
                }

                @Override
                protected void done() {
                    try {
                        filterPhase2(text, this.get());
                    } catch (final Throwable th) {
                        th.printStackTrace();
                    } finally {
                        filterPhase3(text);
                    }
                }
            };

            task.execute();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    sem.countDown();
                    filterPhase0(text);
                }
            });
        } else {
            filterPhase0(text);
            filterPhase2(text, filterPhase1(text));
            filterPhase3(text);
        }
    }

    protected void filterPhase0(final String text) {
        for (final ITreeFilterListener l : m_filterListeners) {
            l.startFiltering(text);
        }
    }

    protected AbstractTreeModel filterPhase1(final String text) {
        final AbstractTreeModel original = getOriginalModel();
        AbstractTreeModel actualModel = original;
        if (LengthUtils.isNotEmpty(text)) {
            actualModel = original.filter(text);
        }
        return actualModel;
    }

    protected void filterPhase2(final String text, final AbstractTreeModel actualModel) {
        setTreeModel(actualModel);
        firePropertyChange("actualModel", null, actualModel);
    }

    protected void filterPhase3(final String text) {
        finishTransaction();

        final TreePath selectionPath = TreeEx.this.getSelectionPath();
        if (selectionPath != null) {
            TreeEx.this.scrollPathToVisible(selectionPath);
        }

        for (final ITreeFilterListener l : m_filterListeners) {
            l.finishFiltering(text);
        }
    }

    /**
     * This method sets the model of tree
     *
     * @param newModel
     *            model of tree
     */
    @Override
    public void setModel(final TreeModel newModel) {
        m_original = (AbstractTreeModel) newModel;
        setTreeModel(newModel);
    }

    /**
     * This method sets the model of tree
     *
     * @param newModel
     *            model of tree
     */
    protected void setTreeModel(final TreeModel newModel) {
        final TreeModel oldModel = getModel();
        if (oldModel instanceof AbstractTreeModel) {
            ((AbstractTreeModel) oldModel).removeTreeTransactionListener(this);
        }
        super.setModel(newModel);

        if (newModel instanceof AbstractTreeModel) {
            ((AbstractTreeModel) newModel).addTreeTransactionListener(this);
        }
    }

    /**
     * Gets original model of tree
     *
     * @return m_original original model of tree
     */
    public AbstractTreeModel getOriginalModel() {
        if (m_original == null) {
            m_original = (AbstractTreeModel) getModel();
        }
        return m_original;
    }

    /**
     * @param model
     *            model of tree
     * @param expectedUserObject
     *            user object
     * @return node or null
     */
    public AbstractTreeNode<?> getNode(final TreeModel model, final Object expectedUserObject) {
        final AbstractTreeNode<?> root = (AbstractTreeNode<?>) model.getRoot();
        for (final Enumeration<AbstractTreeNode<?>> e = root.depthFirstEnumeration(); e.hasMoreElements();) {
            final AbstractTreeNode<?> node = e.nextElement();
            if (LengthUtils.equals(node.getObject(), expectedUserObject)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Retrieves an appropriate tree node.
     *
     * @param model
     *            tree model
     * @param expectedNode
     *            original node
     * @return an instance of the {@link AbstractTreeNode} or <code>null</code>
     */
    public AbstractTreeNode<?> getNode(final TreeModel model, final AbstractTreeNode<?> expectedNode) {
        final AbstractTreeNode<?> root = (AbstractTreeNode<?>) model.getRoot();
        for (final Enumeration<AbstractTreeNode<?>> e = root.depthFirstEnumeration(); e.hasMoreElements();) {
            final AbstractTreeNode<?> node = e.nextElement();
            if (LengthUtils.equals(node, expectedNode)) {
                return node;
            }
        }
        return null;
    }

    /**
     * @return the selected node
     */
    public AbstractTreeNode<?> getSelectedNode() {
        final TreePath selection = getSelectionPath();
        if (selection != null) {
            final Object lastNode = selection.getLastPathComponent();
            if (lastNode instanceof AbstractTreeNode<?>) {
                return (AbstractTreeNode<?>) lastNode;
            }
        }
        return null;
    }

    /**
     * Selects a node containing the given user object
     *
     * @param nodeToSelect
     *            tree node to select
     */
    public void setSelectedNode(final AbstractTreeNode<?> nodeToSelect) {
        final AbstractTreeNode<?> node = getNode(getModel(), nodeToSelect);
        if (node != null) {
            setSelectionPath(new TreePath(node.getPath()));
        }
    }

    /**
     * @return last selected path containing node user objects
     */
    public Object[] getLastSelectedObjectPath() {
        return m_lastSelectedPath;
    }

    /**
     * Retrieves node showing in the given row.
     *
     * @param row
     *            tree row
     * @return an instance of the {@link AbstractTreeNode} or <code>null</code>
     */
    public AbstractTreeNode<?> getNodeForRow(final int row) {
        final TreePath pathForRow = getPathForRow(row);
        if (pathForRow != null) {
            return (AbstractTreeNode<?>) pathForRow.getLastPathComponent();
        }
        return null;
    }

    /**
     * Starts tree changes.
     */
    public void startTransaction(final boolean saveState) {
        if (m_transactionCounter.incrementAndGet() == 1) {
            m_transactionState.compareAndSet(null, saveState ? new TreeState(this) : null);
        }
    }

    public boolean inTransaction() {
        return m_transactionCounter.get() > 0;
    }

    /**
     * Finish tree changes.
     */
    public void finishTransaction() {
        if (m_transactionCounter.decrementAndGet() == 0) {
            final TreeState state = m_transactionState.getAndSet(null);
            if (state != null) {
                state.restore(this);
                m_selectionModel.release();
            } else {
                m_selectionModel.finishTransaction();
            }
        }
    }

    /**
     * Starts tree changes.
     *
     * @param model
     *            tree model
     * @see ITreeTransactionListener#start(AbstractTreeModel)
     */
    public void start(final AbstractTreeModel model) {
        startTransaction(true);
    }

    /**
     * Finish tree changes.
     *
     * @param model
     *            tree model
     * @see ITreeTransactionListener#finish(AbstractTreeModel)
     */
    public void finish(final AbstractTreeModel model) {
        finishTransaction();
    }

    public void addTreeFilterListener(final ITreeFilterListener l) {
        if (!m_filterListeners.contains(l)) {
            m_filterListeners.add(l);
        }
    }

    public void removeTreeFilterListener(final ITreeFilterListener l) {
        m_filterListeners.remove(l);
    }

    /**
     * Class TreeListener
     */
    public final class TreeListener implements TreeSelectionListener {
        /**
         * Called whenever the value of the selection changes.
         *
         * @param e
         *            the event that characterizes the change.
         * @see TreeSelectionListener#valueChanged(TreeSelectionEvent)
         */
        public void valueChanged(final TreeSelectionEvent e) {
            final AbstractTreeNode<?> selectedNode = getSelectedNode();
            if (selectedNode != null) {
                m_lastSelectedPath = selectedNode.getUserObjectPath();
            }
        }
    }

    static enum SelectionEventType {
        ADD, REMOVE, SET, CLEAR;
    }

    static class SelectionEvent {
        final SelectionEventType type;
        final TreePath[] paths;

        SelectionEvent(final SelectionEventType type, final TreePath[] paths) {
            super();
            this.type = type;
            this.paths = paths;
        }
    }

    class ProxySelectionModel implements TreeSelectionModel {

        private final TreeSelectionModel model;

        private SelectionEvent setEvent = null;
        private final Queue<SelectionEvent> otherEvents = new LinkedList<SelectionEvent>();

        ProxySelectionModel(final TreeSelectionModel model) {
            this.model = model;
        }

        public void finishTransaction() {
            if (setEvent != null) {
                execute(setEvent);
                setEvent = null;
            } else {
                for (SelectionEvent event = otherEvents.poll(); event != null; event = otherEvents.poll()) {
                    execute(event);
                }
            }
        }

        public void release() {
            setEvent = null;
            otherEvents.clear();
        }

        void execute(final SelectionEvent event) {
            switch (event.type) {
            case ADD:
                addSelectionPaths(event.paths);
                return;
            case REMOVE:
                removeSelectionPaths(event.paths);
                return;
            case SET:
                setSelectionPaths(event.paths);
                return;
            case CLEAR:
                clearSelection();
                return;
            }
        }

        @Override
        public void addSelectionPath(final TreePath path) {
            if (path != null) {
                final TreePath[] toAdd = new TreePath[1];
                toAdd[0] = path;
                addSelectionPaths(toAdd);
            }
        }

        @Override
        public void addSelectionPaths(final TreePath[] paths) {
            if (inTransaction()) {
                if (this.getSelectionMode() == SINGLE_TREE_SELECTION) {
                    otherEvents.clear();
                    setEvent = new SelectionEvent(SelectionEventType.ADD, paths);
                } else {
                    otherEvents.add(new SelectionEvent(SelectionEventType.ADD, paths));
                }
            } else {
                model.addSelectionPaths(paths);
            }
        }

        @Override
        public void removeSelectionPath(final TreePath path) {
            if (path != null) {
                final TreePath[] rPath = new TreePath[1];
                rPath[0] = path;
                removeSelectionPaths(rPath);
            }
        }

        @Override
        public void removeSelectionPaths(final TreePath[] paths) {
            if (inTransaction()) {
                if (this.getSelectionMode() == SINGLE_TREE_SELECTION) {
                    otherEvents.clear();
                    setEvent = new SelectionEvent(SelectionEventType.REMOVE, paths);
                } else {
                    otherEvents.add(new SelectionEvent(SelectionEventType.REMOVE, paths));
                }
            } else {
                model.removeSelectionPaths(paths);
            }
        }

        @Override
        public void setSelectionPath(final TreePath path) {
            if (path == null) {
                setSelectionPaths(null);
            } else {
                final TreePath[] newPaths = new TreePath[1];
                newPaths[0] = path;
                setSelectionPaths(newPaths);
            }
        }

        @Override
        public void setSelectionPaths(final TreePath[] paths) {
            if (inTransaction()) {
                otherEvents.clear();
                setEvent = new SelectionEvent(SelectionEventType.SET, paths);
            } else {
                model.setSelectionPaths(paths);
            }
        }

        @Override
        public void clearSelection() {
            if (inTransaction()) {
                otherEvents.add(new SelectionEvent(SelectionEventType.CLEAR, null));
            } else {
                model.clearSelection();
            }
        }

        @Override
        public void resetRowSelection() {
            model.resetRowSelection();
        }

        @Override
        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            model.addPropertyChangeListener(listener);
        }

        @Override
        public void addTreeSelectionListener(final TreeSelectionListener x) {
            model.addTreeSelectionListener(x);
        }

        @Override
        public TreePath getLeadSelectionPath() {
            return model.getLeadSelectionPath();
        }

        @Override
        public int getLeadSelectionRow() {
            return model.getLeadSelectionRow();
        }

        @Override
        public int getMaxSelectionRow() {
            return model.getMaxSelectionRow();
        }

        @Override
        public int getMinSelectionRow() {
            return model.getMinSelectionRow();
        }

        @Override
        public RowMapper getRowMapper() {
            return model.getRowMapper();
        }

        @Override
        public int getSelectionCount() {
            return model.getSelectionCount();
        }

        @Override
        public int getSelectionMode() {
            return model.getSelectionMode();
        }

        @Override
        public TreePath getSelectionPath() {
            return model.getSelectionPath();
        }

        @Override
        public TreePath[] getSelectionPaths() {
            return model.getSelectionPaths();
        }

        @Override
        public int[] getSelectionRows() {
            return model.getSelectionRows();
        }

        @Override
        public boolean isPathSelected(final TreePath path) {
            return model.isPathSelected(path);
        }

        @Override
        public boolean isRowSelected(final int row) {
            return model.isRowSelected(row);
        }

        @Override
        public boolean isSelectionEmpty() {
            return model.isSelectionEmpty();
        }

        @Override
        public void removePropertyChangeListener(final PropertyChangeListener listener) {
            model.removePropertyChangeListener(listener);
        }

        @Override
        public void removeTreeSelectionListener(final TreeSelectionListener x) {
            model.removeTreeSelectionListener(x);
        }

        @Override
        public void setRowMapper(final RowMapper newMapper) {
            model.setRowMapper(newMapper);
        }

        @Override
        public void setSelectionMode(final int mode) {
            model.setSelectionMode(mode);
        }
    }
}
