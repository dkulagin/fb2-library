/**
 *
 */
package org.ak2.gui.controls.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ak2.gui.actions.ActionController;
import org.ak2.gui.actions.ActionEx;
import org.ak2.gui.actions.ActionMethod;
import org.ak2.gui.controls.ComponentFactory;
import org.ak2.utils.LengthUtils;

public class FilterField extends JPanel {
    private static final long serialVersionUID = 8413599582950582991L;

    /**
     * Name of text bean property
     */
    public static final String TEXT_PROPERTY = "text";

    private static final Dimension BUTTON_SIZE = new Dimension(22, 22);

    private JTextField jTextField;

    private String m_text = LengthUtils.safeString();

    private boolean m_onlineFilter = true;

    private ActionController m_controller;

    /**
     * Constructor
     *
     * @param key
     *            localization key
     */
    public FilterField() {
        this(false);
    }

    /**
     * Constructor
     *
     * @param key
     *            localization key
     * @param onlineFilter
     *            online filter flag
     */
    public FilterField(final boolean onlineFilter) {
        this(onlineFilter, null);
    }

    /**
     * Constructor
     *
     * @param key
     *            localization key
     * @param onlineFilter
     *            online filter flag
     */
    public FilterField(final boolean onlineFilter, ActionController controller) {
        m_onlineFilter = onlineFilter;
        m_controller = controller != null ? controller : new ActionController("FilterField");
        ActionController.setController(this, m_controller);
        initialize();
    }

    /**
     * Updates text property with text field value
     *
     * @param action
     *            filter action
     */
    @ActionMethod(ids = "filter")
    public void filter(final ActionEx action) {
        updateText();
    }

    /**
     * Clear filter text field
     *
     * @param action
     *            clear action
     */
    @ActionMethod(ids = "clear")
    public void clear(final ActionEx action) {
        setText(LengthUtils.safeString());
        if (!isOnlineFilter()) {
            filter(action);
        }
    }

    /**
     * @return the text
     */
    public String getText() {
        return m_text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(final String text) {
        this.getTextField().setText(LengthUtils.safeString(text));
        this.getTextField().setCaretPosition(LengthUtils.length(text));
    }

    /**
     * Updates actions states
     */
    private void updateText() {
        final String oldText = m_text;
        m_text = getTextField().getText();
        getClearAction().setEnabled(LengthUtils.isNotEmpty(m_text));
        this.firePropertyChange(TEXT_PROPERTY, oldText, m_text);
    }

    /**
     * @return the onlineFilter
     */
    public boolean isOnlineFilter() {
        return m_onlineFilter;
    }

    /**
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());

        ActionEx filterAction = getFilterAction();

        final String title = filterAction.getText();
        final String desc = filterAction.getDescription();
        final Icon filterIcon = filterAction.getIcon();

        if (isOnlineFilter() && filterIcon != null) {
            final JLabel filterLabel = new JLabel();
            filterLabel.setIcon(filterIcon);
            filterLabel.setToolTipText(desc);
            this.add(filterLabel, BorderLayout.WEST);
        }

        final JTextField textField = getTextField();
        textField.setToolTipText(desc);
        this.add(textField, BorderLayout.CENTER);

        final FlowLayout flowLayout = new FlowLayout(FlowLayout.LEADING, 0, 0);
        final JPanel buttonPanel = ComponentFactory.createPanel("ButtonPanel", flowLayout);

        if (!isOnlineFilter()) {
            final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            filterAction.installAction(getTextField(), keyStroke);
            buttonPanel.add(createButton(filterAction));
        }

        ActionEx clearAction = getClearAction();
        if (clearAction != null) {
            clearAction.setEnabled(false);
            final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_MASK);
            clearAction.installAction(getTextField(), keyStroke);
            buttonPanel.add(createButton(clearAction));
        }

        this.add(buttonPanel, BorderLayout.EAST);

        if (LengthUtils.isNotEmpty(title)) {
            this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
        } else {
            this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        }
    }

    /**
     * @return an instance of the {@link JTextField} class
     */
    public JTextField getTextField() {
        if (jTextField == null) {
            jTextField = new JTextField();
            jTextField.getDocument().addDocumentListener(new FilterFieldListener());
        }
        return jTextField;
    }

    /**
     * Creates a button for the given action.
     *
     * @param action
     *            the action
     *
     * @return JButton
     */
    private JButton createButton(final ActionEx action) {
        final JButton button = new JButton();
        button.putClientProperty("hideActionText", true);
        button.setAction(action);
        button.setFocusable(false);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
        button.setBorder(null);

        return button;
    }

    /**
     * @return an instance of the {@link ActionEx} class
     */
    private ActionEx getClearAction() {
        return m_controller.getAction("clear");
    }

    /**
     * @return an instance of the {@link ActionEx} class
     */
    private ActionEx getFilterAction() {
        return m_controller.getAction("filter");
    }

    /**
     * Class FilterFieldListener Contains methods for different listeners
     */
    public final class FilterFieldListener implements DocumentListener {
        /**
         * Called when data is changed
         *
         * @param e
         *            document event
         */
        public void changedUpdate(final DocumentEvent e) {
            if (isOnlineFilter()) {
                updateText();
            }
        }

        /**
         * Called when inserted data
         *
         * @param e
         *            document event
         */
        public void insertUpdate(final DocumentEvent e) {
            if (isOnlineFilter()) {
                updateText();
            }
        }

        /**
         * Called when removed data
         *
         * @param e
         *            document event
         */
        public void removeUpdate(final DocumentEvent e) {
            if (isOnlineFilter()) {
                updateText();
            }
        }
    }

}
