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

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.fb2.shelf.gui.models.catalog.ShelfCatalogModel;
import org.ak2.fb2.shelf.gui.models.tree.AuthorFilterNode;
import org.ak2.fb2.shelf.gui.models.tree.ShelfFilterModel;
import org.ak2.gui.controls.table.TableEx;
import org.ak2.gui.controls.table.policies.ContentResizePolicy;
import org.ak2.gui.controls.tree.TreeEx;

public class MainFrame extends JFrame {

    private static final File XML_CATALOG = new File("/home/whippet/Work/0000.My/FictionBook/SRC/FB2 Library/Sources/FB2 Library/catalog.xml");

    private static final long serialVersionUID = 3595272628118286814L;

    private final Object lock = new Object();

    private ShelfCatalogModel tableModel;

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
        contentPane.add(getLeftSplitPane());

        addWindowListener(new WindowAdapter() {
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
                    AuthorFilterNode node = (AuthorFilterNode) tree.getSelectedNode();
                    if (node == null || node.getObject() == null) {
                        getTableModel().setFilter(null);
                    } else {
                        getTableModel().setFilter(node);
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