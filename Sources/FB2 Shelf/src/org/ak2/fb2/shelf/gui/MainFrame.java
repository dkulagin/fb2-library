/**
 *
 */
package org.ak2.fb2.shelf.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
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
import org.ak2.gui.controls.table.policies.ContentResizePolicy;
import org.ak2.gui.controls.tree.TreeEx;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.tree.AbstractTreeNode;

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
                    protected JComponent doInBackground() throws Exception {
                        return getLeftSplitPane();
                    }

                    @Override
                    protected void done() {
                        getMainPanel().removeAll();
                        getMainPanel().add(getLeftSplitPane(), BorderLayout.CENTER);
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
                @SuppressWarnings("unchecked")
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    AbstractTreeNode<?> node = tree.getSelectedNode();
                    if (node instanceof IEntityFilter<?>) {
                        TreeNode[] path = node.getPath();
                        IEntityFilter<BookInfo>[] filters = new IEntityFilter[path.length];
                        for (int i = 0; i < path.length; i++) {
                            filters[i] = (IEntityFilter<BookInfo>) path[i];
                        }
                        getTableModel().setFilter(filters);
                    } else {
                        getTableModel().setFilter();
                    }
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
            bookTable.setResizePolicy(new ContentResizePolicy());

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