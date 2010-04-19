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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTML;
import javax.swing.tree.TreeSelectionModel;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.fb2.shelf.gui.models.catalog.ShelfCatalogModel;
import org.ak2.fb2.shelf.gui.models.tree.ShelfFilterModel;
import org.ak2.fb2.shelf.gui.renderers.FilterTreeDecorator;
import org.ak2.gui.controls.table.TableEx;
import org.ak2.gui.controls.table.policies.WeightResizePolicy;
import org.ak2.gui.controls.tree.TreeEx;
import org.ak2.gui.models.table.impl.CompositeTableModel;
import org.ak2.gui.models.table.impl.IEntityFilter;
import org.ak2.gui.models.table.impl.TableModelEx;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.html.HtmlBuilder;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class MainFrame extends JFrame {

    private static final JLogMessage MSG_SELECTION_EVENT = new JLogMessage(JLogLevel.DEBUG, "Tree node selected");

    private static final JLogMessage MSG_SELECTION = new JLogMessage(JLogLevel.DEBUG, "Tree selection: ");

    private static final JLogMessage MSG_DLG_CREATED = new JLogMessage(JLogLevel.DEBUG, "Info dialog created.");

    private static final JLogMessage MSG_WORKER_STARTED = new JLogMessage(JLogLevel.DEBUG, "Worker started.");

    private static final JLogMessage MSG_WORKER_FINISHED = new JLogMessage(JLogLevel.DEBUG, "Worker finished.");

    private static final JLogMessage MSG_MODEL_SET = new JLogMessage(JLogLevel.DEBUG, "New model set.");

    private static final JLogMessage MSG_DLG_SHOWING = new JLogMessage(JLogLevel.DEBUG, "Info dialog showing...");

    private static final File XML_CATALOG = new File("catalog.xml");

    private static final long serialVersionUID = 3595272628118286814L;

    private final Object lock = new Object();

    private ShelfCatalogModel tableModel;

    private JPanel mainPanel;

    private TableEx bookTable;

    private JScrollPane bookTableScrollPane;

    private ShelfCatalog m_catalog;

    private JScrollPane treeScrollPane;

    private TreeEx filterTree;

    private ShelfFilterModel treeModel;

    private JSplitPane leftSplitPane;

    private JDialog selectedDlg;

    private JLabel selectionLabel;

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
            treeScrollPane = new JScrollPane(getFilterTree());
            treeScrollPane.setName("treeScrollPane");
        }
        return treeScrollPane;
    }

    private TreeEx getFilterTree() {
        if (filterTree == null) {
            filterTree = new TreeEx();
            filterTree.setName("filterTree");

            FilterTreeDecorator.decorate(filterTree);

            filterTree.setModel(getTreeModel());
            filterTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            filterTree.getSelectionModel().addTreeSelectionListener(new TreeListener());
        }

        return filterTree;
    }

    private JDialog createDialog(final IEntityFilter<BookInfo>[] ffilters) {
        if (selectedDlg == null) {
            selectedDlg = new JDialog(MainFrame.this);
            selectedDlg.setTitle("Please wait...");
            selectedDlg.setModal(true);
            selectedDlg.setUndecorated(true);
            selectedDlg.getContentPane().setLayout(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 16, 8, 16);
            selectedDlg.getContentPane().add(getSelectionLabel(), c);
        }

        JLabel label = getSelectionLabel();
        HtmlBuilder buf = new HtmlBuilder().start();
        buf.start(HTML.Tag.DIV).text("Selected ");
        if (LengthUtils.isEmpty(ffilters)) {
            buf.text("All");
        } else {
            buf.end().start(HTML.Tag.UL);
            for (int i = 0; i < ffilters.length; i++) {
                buf.start(HTML.Tag.LI).text(ffilters[i].toString()).end();
            }
        }
        label.setText(buf.finish());
        selectedDlg.pack();
        selectedDlg.setLocationRelativeTo(this);
        return selectedDlg;
    }

    private JLabel getSelectionLabel() {
        if (selectionLabel == null) {
            selectionLabel = new JLabel();
            selectionLabel.setName("label");
        }
        return selectionLabel;
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

            bookTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) {
                        int selectedRow = getBookTable().getSelectedRow();
                        if (selectedRow == -1) {
                            return;
                        }

                        BookInfo entity = getTableModel().getEntity(selectedRow);

                        new JLogMessage(JLogLevel.DEBUG, "Selected book: {0}").log(entity);

                        File location = new File(entity.getLocation());
                        if (!location.exists()) {
                            JOptionPane.showMessageDialog(MainFrame.this, "Selected base location not found: \n" + location, "Opening book...", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        File container = new File(location, entity.getContainer());
                        if (!container.exists()) {
                            JOptionPane.showMessageDialog(MainFrame.this, "Selected book container not found: \n" + container, "Opening book...", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        File book = container;
                        if (container.isDirectory()) {
                            book = new File(container, entity.getFile());
                        }
                        if (!book.exists()) {
                            JOptionPane.showMessageDialog(MainFrame.this, "Selected book file not found: \n" + book, "Opening book...", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        String fb2readerProperty = System.getProperty("fb2.reader");
                        if (LengthUtils.isEmpty(fb2readerProperty)) {
                            JOptionPane.showMessageDialog(MainFrame.this, "The 'fb2.reader' system property is not set", "Opening book...", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        File fb2reader = new File(fb2readerProperty);
                        if (!fb2reader.exists()) {
                            JOptionPane.showMessageDialog(MainFrame.this, "FB2 Reader program not found: \n" + fb2reader, "Opening book...", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        try {
                            String[] cmdarray = { fb2reader.getCanonicalPath(), book.getCanonicalPath() };
                            Runtime.getRuntime().exec(cmdarray, null, fb2reader.getParentFile());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
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

    private final class TreeListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            MSG_SELECTION_EVENT.log();

            try {
                AbstractTreeNode<?> node = filterTree.getSelectedNode();
                final IEntityFilter<BookInfo>[] ffilters = getFilter(node);

                MSG_SELECTION.log(Arrays.toString(ffilters));

                final JDialog dlg = createDialog(ffilters);

                MSG_DLG_CREATED.log();

                final long startTime = System.currentTimeMillis();

                final SwingWorker<TableModelEx<BookInfo, ?>, String> task = new SwingWorker<TableModelEx<BookInfo, ?>, String>() {
                    @Override
                    protected TableModelEx<BookInfo, ?> doInBackground() {
                        MSG_WORKER_STARTED.log();
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
                        MSG_WORKER_FINISHED.log();
                        try {
                            getBookTable().setModel(this.get());

                            final long endTime = System.currentTimeMillis();
                            final long delta = (startTime + 500) - endTime;
                            if (delta > 50) {
                                Thread.sleep(delta);
                            }

                            getFilterTree().setEnabled(true);
                            dlg.setVisible(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        MSG_MODEL_SET.log();
                    }
                };
                task.execute();

                MSG_DLG_SHOWING.log();
                getFilterTree().setEnabled(false);
                dlg.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @SuppressWarnings("unchecked")
        private IEntityFilter<BookInfo>[] getFilter(AbstractTreeNode<?> node) {
            IEntityFilter<BookInfo>[] filters = null;
            if (node instanceof IEntityFilter<?>) {
                filters = new IEntityFilter[1];
                filters[0] = (IEntityFilter<BookInfo>) node;
            }
            return filters;
        }
    }
}