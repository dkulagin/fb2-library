/**
 *
 */
package org.ak2.fb2.shelf.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.fb2.shelf.gui.models.catalog.ShelfCatalogModel;
import org.ak2.fb2.shelf.gui.models.tree.ShelfFilterModel;
import org.ak2.gui.controls.table.TableEx;
import org.ak2.gui.controls.table.policies.WeightResizePolicy;
import org.ak2.gui.controls.tree.TreeEx;
import org.ak2.gui.models.table.impl.CompositeTableModel;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.table.impl.TableModelEx;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class MainFrame extends JFrame {

    private static final File XML_CATALOG = new File("catalog.xml");

    private static final long serialVersionUID = 3595272628118286814L;

    private final Object lock = new Object();

    private ShelfCatalogModel tableModel;

    private JPanel mainPanel;

    private TableEx bookTable;

    private JScrollPane bookTableScrollPane;

    private ShelfCatalog m_catalog;

    private JScrollPane treeScrollPane;

    private TreeEx tree;

    private ShelfFilterModel treeModel;

    private JSplitPane leftSplitPane;

    public MainFrame() {
        super("FB2 Shelf");
        setName("MainFrame");
        setPreferredSize(new Dimension(800, 600));

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(getMainPanel(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                final SwingWorker<JComponent, String> task = new SwingWorker<JComponent, String>() {
                    @Override
                    protected JComponent doInBackground() {
                        try {
                            return getLeftSplitPane();
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        getMainPanel().removeAll();
                        try {
                            if (this.get() != null) {
                                getMainPanel().add(getLeftSplitPane(), BorderLayout.CENTER);
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                        pack();
                    }
                };
                task.execute();
            }

            @Override
            public void windowClosing(WindowEvent e) {
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
            mainPanel.add(new JLabel("Loading book shelf...", SwingConstants.CENTER), BorderLayout.SOUTH);
        }
        return mainPanel;
    }

    private JSplitPane getLeftSplitPane() {
        if (leftSplitPane == null) {
            leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            leftSplitPane.setLeftComponent(getTreeScrollPane());
            leftSplitPane.setRightComponent(getBookTableScrollPane());
            leftSplitPane.setResizeWeight(0.25);
            leftSplitPane.setDividerLocation(0.25);
        }
        return leftSplitPane;
    }

    private JScrollPane getTreeScrollPane() {
        if (treeScrollPane == null) {
            treeScrollPane = new JScrollPane(getTree());
            treeScrollPane.setName("treeScrollPane");
        }
        return treeScrollPane;
    }

    private TreeEx getTree() {
        if (tree == null) {
            tree = new TreeEx();
            tree.setName("tree");

            tree.setModel(getTreeModel());
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    new JLogMessage(JLogLevel.INFO, "Tree node selected").log();

                    try {
                        AbstractTreeNode<?> node = tree.getSelectedNode();
                        final IEntityFilter<BookInfo>[] ffilters = getFilter(node);

                        new JLogMessage(JLogLevel.INFO, "Tree selection: ").log(Arrays.toString(ffilters));

                        final JDialog dlg = createDialog(ffilters);

                        new JLogMessage(JLogLevel.INFO, "Info dialog created.").log();

                        final SwingWorker<TableModelEx<BookInfo, ?>, String> task = new SwingWorker<TableModelEx<BookInfo, ?>, String>() {
                            @Override
                            protected TableModelEx<BookInfo, ?> doInBackground() {
                                new JLogMessage(JLogLevel.INFO, "Worker started.").log();
                                try {
                                    if (LengthUtils.length(ffilters) > 0) {
                                        CompositeTableModel<BookInfo> model = new CompositeTableModel<BookInfo>(getTableModel());
                                        model.setFilter(ffilters);
                                        return model;
                                    }
                                } catch (Throwable th) {
                                    th.printStackTrace();
                                }
                                return getTableModel();
                            }

                            @Override
                            protected void done() {
                                new JLogMessage(JLogLevel.INFO, "Worker finished.").log();
                                try {
                                    getBookTable().setModel(this.get());
                                    getTree().setEnabled(true);
                                    dlg.setVisible(false);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                new JLogMessage(JLogLevel.INFO, "New model set.").log();
                            }
                        };
                        task.execute();

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                new JLogMessage(JLogLevel.INFO, "Info dialog showing...").log();
                                try {
                                    getTree().setEnabled(false);
                                    dlg.setVisible(true);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                private JDialog createDialog(final IEntityFilter<BookInfo>[] ffilters) {
                    final JDialog dlg = new JDialog(MainFrame.this);
                    dlg.setTitle("Please wait...");
                    dlg.setModal(true);
                    dlg.setUndecorated(true);
                    dlg.getContentPane().setLayout(new GridBagLayout());
                    JLabel label = new JLabel();
                    label.setText("<html>Selected: " + (ffilters.length > 0 ? Arrays.toString(ffilters) : "ALL") + "</html>");
                    GridBagConstraints c = new GridBagConstraints();
                    c.insets = new Insets(8, 16, 8, 16);
                    dlg.getContentPane().add(label, c);
                    dlg.pack();
                    dlg.setLocationRelativeTo(MainFrame.this);
                    return dlg;
                }

                @SuppressWarnings("unchecked")
                private IEntityFilter<BookInfo>[] getFilter(AbstractTreeNode<?> node) {
                    IEntityFilter<BookInfo>[] filters = null;
                    if (node instanceof IEntityFilter<?>) {
                        TreeNode[] path = node.getPath();
                        filters = new IEntityFilter[path.length - 1];
                        for (int i = 1; i < path.length; i++) {
                            filters[i - 1] = (IEntityFilter<BookInfo>) path[i];
                        }
                    }
                    return filters;
                }
            });
        }

        return tree;
    }

    private ShelfFilterModel getTreeModel() {
        if (treeModel == null) {
            treeModel = new ShelfFilterModel(getCatalog());
        }
        return treeModel;
    }

    private JScrollPane getBookTableScrollPane() {
        if (bookTableScrollPane == null) {
            bookTableScrollPane = new JScrollPane(getBookTable());
            bookTableScrollPane.setName("bookTableScrollPane");
        }
        return bookTableScrollPane;
    }

    private TableEx getBookTable() {
        if (bookTable == null) {
            bookTable = new TableEx();
            bookTable.setName("bookTable");
            bookTable.setResizePolicy(new WeightResizePolicy(30, 10, 60));
            bookTable.setModel(getTableModel());
        }

        return bookTable;
    }

    private ShelfCatalogModel getTableModel() {
        if (tableModel == null) {
            tableModel = new ShelfCatalogModel(getCatalog());
        }
        return tableModel;
    }

    private ShelfCatalog getCatalog() {
        if (m_catalog == null) {
            m_catalog = new ShelfCatalog(XML_CATALOG);
        }
        return m_catalog;
    }
}