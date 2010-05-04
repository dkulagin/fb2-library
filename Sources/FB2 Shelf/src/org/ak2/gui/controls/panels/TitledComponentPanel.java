/**
 *
 */
package org.ak2.gui.controls.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import org.ak2.gui.actions.ActionController;
import org.ak2.gui.actions.ActionEx;
import org.ak2.gui.actions.ActionMethod;
import org.ak2.gui.controls.ComponentFactory;
import org.ak2.utils.LengthUtils;

/**
 * @param <Inner>
 *            type of inner component
 * @author Whippet
 */
public abstract class TitledComponentPanel<Inner extends JComponent> extends JPanel {
    private static final long serialVersionUID = -7346340248070613585L;

    private JScrollPane jScrollPane;

    private JPanel jHeader;

    private JLabel jTitleLabel;

    private Inner jInner;

    private JPopupMenu jPopupMenu;

    private ActionController m_controller;

    private FilterField jFilterField;

    /**
     * Constructor.
     *
     * @param parent
     *            parent controller
     */
    public TitledComponentPanel() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param parent
     *            parent controller
     * @param filterField
     *            filter field
     */
    public TitledComponentPanel(final FilterField filterField) {
        m_controller = new ActionController(this.getClass().getSimpleName());
        ActionController.setController(this, m_controller);
        jFilterField = filterField;
        initialize();
    }

    /**
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout()); // Generated

        if (jFilterField != null) {
            final JPanel topPanel = ComponentFactory.createPanel("TopPanel", new BorderLayout());
            topPanel.add(getHeader(), BorderLayout.NORTH);
            topPanel.add(jFilterField, BorderLayout.SOUTH);
            this.add(topPanel, BorderLayout.NORTH);
        } else {
            this.add(getHeader(), BorderLayout.NORTH);
        }
        this.add(getScrollPane(), BorderLayout.CENTER);
    }

    /**
     * @return the value of the <code>minimumSize</code> property
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
        return getHeader().getMinimumSize();
    }

    /**
     * Calculates preferred size of the component
     *
     * @return an instance of the {@link Dimension} object
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        final Dimension hps = getHeader().getPreferredSize();
        final Dimension ips = getInner().getPreferredSize();
        final Dimension ps = super.getPreferredSize();
        final Dimension vps = getScrollPane().getVerticalScrollBar().getPreferredSize();

        final int width = Math.max(ps.width, Math.max(hps.width, ips.width) + vps.width);
        final int height = Math.max(ps.height, hps.height + ips.height);

        return new Dimension(width, height);
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return jTitleLabel.getText();
    }

    /**
     * Sets the title.
     *
     * @param title
     *            the new title
     */
    public void setTitle(final String title) {
        jTitleLabel.setText(title);
    }

    /**
     * @return the filter field
     */
    public FilterField getFilterField() {
        return jFilterField;
    }

    /**
     * Resize node tree
     *
     * @param action
     *            resize action
     */
    @ActionMethod(ids = "resize")
    public void resizeComponent(final ActionEx action) {
        final Container parent = this.getParent();
        if (parent instanceof JSplitPane) {
            final JSplitPane splitPane = (JSplitPane) parent;
            final Dimension ps = getPreferredSize();
            if (splitPane.getLeftComponent() == this) {
                splitPane.setDividerLocation(ps.width);
            } else {
                final int possibleWidth = splitPane.getWidth() - splitPane.getDividerSize();
                splitPane.setDividerLocation(possibleWidth - ps.width);
            }
        }

        parent.doLayout();
    }

    /**
     * Resize node tree
     *
     * @param action
     *            resize action
     */
    @ActionMethod(ids = "popup")
    public void showPopup(final ActionEx action) {
        final JPopupMenu popupMenu = getPopupMenu();
        if (popupMenu == null) {
            return;
        }

        if (popupMenu.isVisible()) {
            popupMenu.setVisible(false);
        } else {
            final ActionEvent originalEvent = action.getOriginalEvent();
            final Component source = (Component) originalEvent.getSource();
            popupMenu.show(source.getParent(), source.getX(), source.getY() + source.getHeight());
        }
    }

    /**
     * This method initializes jTree
     *
     * @return javax.swing.JTree
     */
    public Inner getInner() {
        if (jInner == null) {
            try {
                jInner = createInner();
                ToolTipManager.sharedInstance().registerComponent(jInner);
            } catch (final java.lang.Throwable e) {
                e.printStackTrace();
            }
        }
        return jInner;
    }

    /**
     * Creates an inner component.
     *
     * @return an instance of the {@link JComponent} object
     */
    protected abstract Inner createInner();

    /**
     * Creates a header popup menu
     *
     * @return an instance of the {@link JPopupMenu} object or <code>null</code>
     */
    protected JPopupMenu createPopupMenu() {
        return null;
    }

    /**
     * Creates the actions.
     *
     * @return a list of {@link ActionEx} objects
     */
    protected List<ActionEx> createActions() {
        return new ArrayList<ActionEx>();
    }

    /**
     * Gets the controller.
     *
     * @return the controller
     */
    protected ActionController getController() {
        return m_controller;
    }

    /**
     * This method initializes m_resizeAction
     *
     * @return {@link ActionEx}
     */
    protected ActionEx getResizeAction() {
        return getController().getAction("resize");
    }

    /**
     * This method initializes m_resizeAction
     *
     * @return {@link ActionEx}
     */
    protected ActionEx getPopupAction() {
        return getController().getAction("popup");
    }

    /**
     * This method initializes jTreeHeader
     *
     * @return javax.swing.JPanel
     */
    private JPanel getHeader() {
        if (jHeader == null) {
            try {
                jTitleLabel = new JLabel();
                jTitleLabel.setText(LengthUtils.safeString());
                jTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                jHeader = ComponentFactory.createPanel("TreeHeader", new GridBagLayout());
                jHeader.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

                int column = 0;

                final GridBagConstraints treeTitleLabelConstraints = new GridBagConstraints();
                treeTitleLabelConstraints.gridy = 0;
                treeTitleLabelConstraints.gridx = column++;
                treeTitleLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
                treeTitleLabelConstraints.weightx = 1.0;

                final List<ActionEx> actions = createActions();
                actions.add(getResizeAction());

                if (null != getPopupMenu()) {
                    actions.add(getPopupAction());
                }

                jHeader.add(jTitleLabel, treeTitleLabelConstraints);
                for (final ActionEx action : actions) {
                    if (action != null) {
                        final GridBagConstraints buttonConstraints = new GridBagConstraints();
                        buttonConstraints.gridy = 0;
                        buttonConstraints.gridx = column++;

                        jHeader.add(createButton(action), buttonConstraints);
                    }
                }
            } catch (final java.lang.Throwable e) {
                e.printStackTrace();
            }
        }
        return jHeader;
    }

    /**
     * This method creates an action button
     *
     * @param action
     *            button action
     * @return javax.swing.JButton
     */
    private JButton createButton(final ActionEx action) {
        final JButton button = ComponentFactory.createIconButton(16, action);
        button.setFocusable(false);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * This method initializes jTreeScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (jScrollPane == null) {
            try {
                jScrollPane = new JScrollPane();
                jScrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Generated
                jScrollPane.setViewportView(getInner()); // Generated
                // jTreeScrollPane.setColumnHeaderView(getHeader());
            } catch (final java.lang.Throwable e) {
                e.printStackTrace();
            }
        }
        return jScrollPane;
    }

    /**
     * @return a popup menu
     */
    private JPopupMenu getPopupMenu() {
        if (jPopupMenu == null) {
            jPopupMenu = createPopupMenu();
        }
        return jPopupMenu;
    }
}
