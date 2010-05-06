/**
 *
 */
package org.ak2.fb2.shelf.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.html.HTML;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.FileInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.fb2.shelf.catalog.ShelfCatalogProvider;
import org.ak2.fb2.shelf.gui.models.catalog.ShelfCatalogModel;
import org.ak2.fb2.shelf.gui.models.tree.AbstractBooksNode;
import org.ak2.fb2.shelf.gui.models.tree.RootFilterNode;
import org.ak2.fb2.shelf.gui.models.tree.ShelfFilterModel;
import org.ak2.fb2.shelf.gui.renderers.FilterTreeDecorator;
import org.ak2.gui.controls.panels.FilterField;
import org.ak2.gui.controls.panels.TitledTablePanel;
import org.ak2.gui.controls.panels.TitledTreePanel;
import org.ak2.gui.controls.table.TableEx;
import org.ak2.gui.controls.table.policies.WeightResizePolicy;
import org.ak2.gui.controls.tree.ITreeFilterListener;
import org.ak2.gui.controls.tree.TreeEx;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.html.HtmlBuilder;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class MainFrame extends JFrame {

    private static final JLogMessage MSG_TREE_EVENT = new JLogMessage(JLogLevel.DEBUG, "Tree event: {0}");

    private static final JLogMessage MSG_SELECTION = new JLogMessage(JLogLevel.DEBUG, "Filter node selected: {0}");

    private static final JLogMessage MSG_WORKER_STARTED = new JLogMessage(JLogLevel.DEBUG, "Worker started.");

    private static final JLogMessage MSG_WORKER_FINISHED = new JLogMessage(JLogLevel.DEBUG, "Worker finished.");

    private static final JLogMessage MSG_MODEL_SET = new JLogMessage(JLogLevel.DEBUG, "New model set.");

    private static final JLogMessage MSG_DLG_SHOWING = new JLogMessage(JLogLevel.DEBUG, "Info dialog showing...");

    private static final JLogMessage MSG_SELECTED_BOOK = new JLogMessage(JLogLevel.DEBUG, "Selected book: {0}");

    private static final long serialVersionUID = 3595272628118286814L;

    private final Object lock = new Object();

    private ShelfCatalogModel tableModel;

    private JPanel mainPanel;

    private TitledTablePanel tablePanel;

    private ShelfCatalog m_catalog;

    private TitledTreePanel treePanel;

    private ShelfFilterModel treeModel;

    private JSplitPane leftSplitPane;

    private JPanel waitPanel;

    private JLabel waitLabel;

    public MainFrame() {
        super("FB2 Shelf");
        setName("MainFrame");
        setPreferredSize(new Dimension(800, 600));

        setGlassPane(getWaitPanel());

        showWaitMessage("Loading book shelf...");

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(getMainPanel(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(final WindowEvent e) {
                final SwingWorker<JComponent, String> task = new SwingWorker<JComponent, String>() {
                    @Override
                    protected JComponent doInBackground() {
                        return getLeftSplitPane();
                    }

                    @Override
                    protected void done() {
                        try {
                            JComponent jComponent = this.get();
                            if (jComponent != null) {
                                getMainPanel().add(jComponent, BorderLayout.CENTER);
                            }
                        } catch (final Throwable th) {
                            th.printStackTrace();
                        } finally {
                            hideWaitMessage();
                        }
                        pack();
                    }
                };
                task.execute();
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }

            @Override
            public void windowClosed(final WindowEvent e) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        pack();
    }

    public void showFrame() {
        this.setVisible(true);
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (final InterruptedException ex) {
            Thread.interrupted();
        }
    }

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setName("mainPanel");
            mainPanel.setLayout(new BorderLayout());
        }
        return mainPanel;
    }

    private JSplitPane getLeftSplitPane() {
        if (leftSplitPane == null) {
            leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            leftSplitPane.setLeftComponent(getTreePanel());
            leftSplitPane.setRightComponent(getTablePanel());
            leftSplitPane.setResizeWeight(0.25);
            leftSplitPane.setDividerLocation(0.25);
        }
        return leftSplitPane;
    }

    private TitledTreePanel getTreePanel() {
        if (treePanel == null) {
            treePanel = new TitledTreePanel(new FilterField());
            treePanel.setName("treePane");
            treePanel.setTitle("Book shelf");
            treePanel.setParallelFilter(true);

            final TreeEx tree = treePanel.getInner();
            FilterTreeDecorator.decorate(tree);
            tree.setModel(getTreeModel());
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

            final TreeListener x = new TreeListener();
            tree.getSelectionModel().addTreeSelectionListener(x);
            tree.addTreeWillExpandListener(x);
            tree.addTreeFilterListener(x);
        }
        return treePanel;
    }

    private TreeEx getFilterTree() {
        return getTreePanel().getInner();
    }

    private void showWaitMessage(String text) {
        getWaitLabel().setText(text);
        getWaitPanel().setVisible(true);
    }

    private void hideWaitMessage() {
        getWaitPanel().setVisible(false);
    }

    private JPanel getWaitPanel() {
        if (waitPanel == null) {
            waitPanel = new JPanel();
            waitPanel.setName("waitPanel");
            waitPanel.setBackground(new Color(waitPanel.getBackground().getRGB() & 0x3FFFFFFF, true));
            waitPanel.setLayout(new GridBagLayout());
            waitPanel.add(getWaitLabel(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0,
                    0), 0, 0));

            waitPanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    e.consume();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    e.consume();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    e.consume();
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    e.consume();
                }
            });
        }
        return waitPanel;
    }

    private JLabel getWaitLabel() {
        if (waitLabel == null) {
            waitLabel = new JLabel();
            waitLabel.setName("waitLabel");
            waitLabel.setOpaque(true);
            waitLabel.setBackground(new Color(waitLabel.getBackground().getRGB() & 0xBFFFFFFF, true));
        }
        return waitLabel;
    }

    private String getFilterDescription(final String text) {
        if (LengthUtils.isEmpty(text)) {
            return "Remove filter";
        } else {
            return "Search for " + text;
        }
    }

    private String getSelectionDescription(final AbstractTreeNode<?> node) {
        final HtmlBuilder buf = new HtmlBuilder().start();
        buf.start(HTML.Tag.DIV).text("Selected ");

        final Object[] path = node != null ? node.getUserObjectPath() : null;

        if (LengthUtils.length(path) < 2) {
            buf.text("All");
        } else {
            buf.end().start(HTML.Tag.UL);
            for (int i = 1; i < path.length; i++) {
                buf.start(HTML.Tag.LI).text(path[i].toString()).end();
            }
        }
        return buf.finish();
    }

    private String getTableTitle(final AbstractTreeNode<?> node) {
        final Object[] path = (node != null ? node : getTreeModel().getRootNode()).getUserObjectPath();
        if (path.length == 1) {
            return path[0].toString();
        }
        final StringBuilder buf = new StringBuilder();
        for (int i = 1; i < path.length; i++) {
            if (i > 1) {
                buf.append(" :: ");
            }
            buf.append(path[i].toString());
        }
        return buf.toString();
    }

    private ShelfFilterModel getTreeModel() {
        if (treeModel == null) {
            treeModel = new ShelfFilterModel(getCatalog());
        }
        return treeModel;
    }

    private TitledTablePanel getTablePanel() {
        if (tablePanel == null) {
            tablePanel = new TitledTablePanel();
            tablePanel.setName("bookTableScrollPane");
            tablePanel.setTitle(getTableTitle(null));

            final TableEx bookTable = tablePanel.getInner();
            bookTable.setResizePolicy(new WeightResizePolicy(30, 10, 60));
            bookTable.setModel(getTableModel());
            bookTable.addMouseListener(new TableListener());
        }
        return tablePanel;
    }

    private TableEx getBookTable() {
        return getTablePanel().getInner();
    }

    private ShelfCatalogModel getTableModel() {
        if (tableModel == null) {
            tableModel = new ShelfCatalogModel(getCatalog());
        }
        return tableModel;
    }

    private ShelfCatalog getCatalog() {
        if (m_catalog == null) {
            m_catalog = ShelfCatalogProvider.getCatalog();
        }
        return m_catalog;
    }

    private final class TreeListener implements TreeSelectionListener, TreeWillExpandListener, ITreeFilterListener {
        @Override
        public void valueChanged(final TreeSelectionEvent e) {
            MSG_TREE_EVENT.log(e);

            try {
                final AbstractBooksNode<?> node = getFilterNode();

                MSG_SELECTION.log(node);

                final long startTime = System.currentTimeMillis();

                final SwingWorker<ITableModel<BookInfo, ?>, String> task = new SwingWorker<ITableModel<BookInfo, ?>, String>() {
                    @Override
                    protected ITableModel<BookInfo, ?> doInBackground() {
                        MSG_WORKER_STARTED.log();
                        if (node != null) {
                            try {
                                return node.getBooksModel();
                            } catch (final Throwable th) {
                                th.printStackTrace();
                            }
                        }
                        return getTableModel();
                    }

                    @Override
                    protected void done() {
                        MSG_WORKER_FINISHED.log();
                        try {
                            getBookTable().setModel(this.get());

                            final long endTime = System.currentTimeMillis();
                            final long delta = (startTime + 500) - endTime;
                            if (delta > 50) {
                                Thread.sleep(delta);
                            }

                            tablePanel.setTitle(getTableTitle(node));

                            getFilterTree().setEnabled(true);
                            hideWaitMessage();

                        } catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                        MSG_MODEL_SET.log();
                    }
                };
                task.execute();

                MSG_DLG_SHOWING.log();
                getFilterTree().setEnabled(false);
                showWaitMessage(getSelectionDescription(node));

            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        private AbstractBooksNode<?> getFilterNode() {
            final AbstractTreeNode<?> selectedNode = treePanel.getInner().getSelectedNode();
            if (selectedNode instanceof RootFilterNode) {
                return null;
            }
            if (selectedNode instanceof AbstractBooksNode<?>) {
                return (AbstractBooksNode<?>) selectedNode;
            }
            return null;
        }

        @Override
        public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException {
            // MSG_TREE_EVENT.log(event);

            final TreePath collapsingPath = event.getPath();
            final Object[] path = collapsingPath.getPath();
            final AbstractTreeNode<?> selectedNode = getFilterTree().getSelectedNode();
            if (selectedNode != null) {
                final TreeNode[] selectedPath = selectedNode.getPath();
                boolean eq = true;
                for (int i = 0, n = Math.min(path.length, selectedPath.length); eq && i < n; i++) {
                    eq = path[i] == selectedPath[i];
                }
                if (eq) {
                    final Object collapsedNode = collapsingPath.getLastPathComponent();
                    getFilterTree().setSelectedNode((AbstractTreeNode<?>) collapsedNode);
                }
            }
        }

        @Override
        public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
        }

        @Override
        public void startFiltering(final String text) {
            showWaitMessage(getFilterDescription(text));
        }

        @Override
        public void finishFiltering(final String text) {
            hideWaitMessage();
        }

    }

    private final class TableListener extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) {
                final int selectedRow = getBookTable().getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }

                final ITableModel<BookInfo, ?> currentModel = getBookTable().getEntityModel();
                final BookInfo entity = currentModel.getEntity(selectedRow);

                MSG_SELECTED_BOOK.log(entity);

                final FileInfo fileInfo = entity.getFileInfo();

                if (!fileInfo.getLocation().exists()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Selected base location not found: \n" + fileInfo.getLocationPath(), "Opening book...",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!fileInfo.getContainer().exists()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Selected book container not found: \n" + fileInfo.getFullContainerPath(), "Opening book...",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!fileInfo.getBook().exists()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Selected book file not found: \n" + fileInfo.getFullBookPath(), "Opening book...",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                final String fb2readerProperty = System.getProperty("fb2.reader");
                if (LengthUtils.isEmpty(fb2readerProperty)) {
                    JOptionPane
                            .showMessageDialog(MainFrame.this, "The 'fb2.reader' system property is not set", "Opening book...", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                final File fb2reader = new File(fb2readerProperty);
                if (!fb2reader.exists()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "FB2 Reader program not found: \n" + fb2reader, "Opening book...",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    final String[] cmdarray = { fb2reader.getCanonicalPath(), fileInfo.getFullBookPath() };
                    Runtime.getRuntime().exec(cmdarray, null, fb2reader.getParentFile());
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}