package org.ak2.gui.controls.panels;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.ak2.gui.actions.ActionEx;
import org.ak2.gui.actions.ActionMethod;
import org.ak2.gui.controls.tree.TreeEx;

public class TitledTreePanel extends TitledComponentPanel<TreeEx> {
    private static final long serialVersionUID = 1577150182197771296L;

    /**
     * Constructor
     *
     * @param parent
     *            parent controller
     */
    public TitledTreePanel() {
        super();
    }

    /**
     * Constructor
     *
     * @param parent
     *            parent controller
     * @param filterField
     *            filter field
     */
    public TitledTreePanel(final FilterField filterField) {
        super(filterField);
    }

    /**
     * Expands all nodes
     *
     * @param action
     *            expand action
     */
    @ActionMethod(ids = "expand")
    public void expandAll(final ActionEx action) {
        getInner().expandAll();
    }

    /**
     * Collapses all nodes
     *
     * @param action
     *            collapse action
     */
    @ActionMethod(ids = "collapse")
    public void collapseAll(final ActionEx action) {
        getInner().collapsAll();
    }

    /**
     * @return an instance of the {@link TreeEx} object
     * @see TitledComponentPanel#createInner()
     */
    @Override
    protected TreeEx createInner() {
        final TreeEx treeEx = new TreeEx();
        treeEx.addKeyListener(new TreeListener());

        final FilterField filterField = getFilterField();
        if (filterField != null) {
            filterField.addPropertyChangeListener(FilterField.TEXT_PROPERTY, new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String filter = (String) evt.getNewValue();
                    treeEx.filter(filter);
                }
            });
        }
        return treeEx;
    }

    /**
     * @return a list of {@link ActionEx} objects
     * @see com.tsystems.wmstk.fqcleanup.ui.controls.TitledComponentPanel#createActions()
     */
    @Override
    protected List<ActionEx> createActions() {
        final List<ActionEx> actions = super.createActions();
        actions.add(getController().getAction("expand"));
        actions.add(getController().getAction("collapse"));
        return actions;
    }

    /**
     * Class TreeListener
     */
    public final class TreeListener extends KeyAdapter {
        /**
         * @param e
         *            key event
         */
        @Override
        public void keyReleased(final KeyEvent e) {
            final FilterField filterField = getFilterField();
            if (filterField != null) {
                final char keyChar = e.getKeyChar();
                if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar >= ' ') {
                    final StringBuilder buf = new StringBuilder(filterField.getText());
                    buf.append(keyChar);
                    filterField.setText(buf.toString());
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    final StringBuilder buf = new StringBuilder(filterField.getText());
                    buf.setLength(Math.max(0, buf.length() - 1));
                    filterField.setText(buf.toString());
                }
            }
        }
    }

}
