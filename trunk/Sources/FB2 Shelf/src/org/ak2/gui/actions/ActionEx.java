package org.ak2.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.ak2.gui.actions.params.IActionParameter;
import org.ak2.utils.LengthUtils;

public class ActionEx extends AbstractAction {

    private static final long serialVersionUID = -8393542555460785906L;

    private static final String PARAMETER_SOURCE_SELECTED = "SourceSelected";

    private static final String PARAMETER_ITEM_SELECTED = "ItemSelected";

    private static final String MNEMONIC_DELIMER = "&";

    private final String m_id;

    private String m_text;

    private ActionEvent m_originalEvent;

    private final ActionController m_controller;

    private final Map<String, IActionParameter> m_actionParameters = new HashMap<String, IActionParameter>();

    /**
     * Constructor
     *
     * @param controller
     *            action controller
     * @param category
     *            action category
     * @param id
     *            action id
     */
    ActionEx(final String id, ActionController controller) {
        m_id = id;
        m_controller = controller;
    }

    /**
     * Returns the action's id.
     *
     * @return the id
     */
    public String getId() {
        return m_id;
    }

    /**
     * Gets the controller.
     *
     * @return the controller
     */
    public ActionController getController() {
        return m_controller;
    }

    /**
     * Returns the action's text.
     *
     * @return the actions text
     * @see #setName
     */
    public String getText() {
        return m_text;
    }

    /**
     * Sets the action's text.
     *
     * @param text
     *            the string used to set the action's name
     * @see #getText
     * @beaninfo bound: true preferred: true attribute: visualUpdate true description: The action's name.
     */
    public void setText(final String text) {
        m_text = text;
        if (LengthUtils.isNotEmpty(text)) {
            putValue(Action.NAME, parseName(text));
            putValue(Action.MNEMONIC_KEY, parseMenumonic(text));
        } else {
            putValue(Action.NAME, null);
            putValue(Action.MNEMONIC_KEY, null);
        }
    }

    /**
     * Returns the default icon.
     *
     * @return the default <code>Icon</code>
     * @see #setIcon
     */
    public Icon getIcon() {
        return (Icon) getValue(Action.SMALL_ICON);
    }

    /**
     * Sets the button's default icon. This icon is also used as the "pressed" and "disabled" icon if there is no explicitly set pressed icon.
     *
     * @param defaultIcon
     *            the icon used as the default image
     * @see #getIcon
     * @beaninfo bound: true attribute: visualUpdate true description: The button's default icon
     */
    public void setIcon(final Icon defaultIcon) {
        putValue(Action.SMALL_ICON, defaultIcon);
    }

    /**
     * Returns the action's accelerator.
     *
     * @return the actions accelerator
     * @see #setDescription
     */
    public KeyStroke getAccelerator() {
        return (KeyStroke) getValue(Action.ACCELERATOR_KEY);
    }

    /**
     * Sets the action's accelerator.
     *
     * @param accelerator
     *            the string used to set the action's accelerator
     * @see #getAccelerator
     * @beaninfo bound: true preferred: true attribute: visualUpdate true description: The action's accelerator.
     */
    public void setAccelerator(final KeyStroke accelerator) {
        putValue(Action.ACCELERATOR_KEY, accelerator);
    }

    /**
     * Returns the action's description.
     *
     * @return the actions description
     * @see #setDescription
     */
    public String getDescription() {
        return (String) getValue(Action.SHORT_DESCRIPTION);
    }

    /**
     * Sets the action's description.
     *
     * @param description
     *            the string used to set the action's description
     * @see #getDescription
     * @beaninfo bound: true preferred: true attribute: visualUpdate true description: The action's name.
     */
    public void setDescription(final String description) {
        putValue(Action.SHORT_DESCRIPTION, description);
    }

    /**
     * @return the originalEvent
     */
    public ActionEvent getOriginalEvent() {
        return m_originalEvent;
    }

    /**
     * @param originalEvent
     *            the originalEvent to set
     */
    void setOriginalEvent(final ActionEvent originalEvent) {
        m_originalEvent = originalEvent;

        Boolean sourceSelected = null;
        Object selectedItem = null;

        if (m_originalEvent != null) {
            final Object source = m_originalEvent.getSource();
            if (source instanceof AbstractButton) {
                sourceSelected = ((AbstractButton) source).isSelected();
            } else if (source instanceof JComboBox) {
                selectedItem = ((JComboBox) source).getSelectedItem();
            }
        }

        this.putValue(PARAMETER_SOURCE_SELECTED, sourceSelected);
        this.putValue(PARAMETER_ITEM_SELECTED, selectedItem);
    }

    /**
     * Creates a fake action event for the given source component
     *
     * @param source
     *            event source component
     */
    void setOriginalEvent(final Component source) {
        setOriginalEvent(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, getId()));
    }

    /**
     * Gets the <code>Object</code> associated with the specified key.
     *
     * @param key
     *            a string containing the specified <code>key</code>
     * @return the binding <code>Object</code> stored with this key; if there are no keys, it will return <code>null</code>
     * @see javax.swing.AbstractAction#getValue(java.lang.String)
     */
    @Override
    @Deprecated
    public Object getValue(final String key) {
        return super.getValue(key);
    }

    /**
     * Gets the <code>Object</code> associated with the specified key.
     *
     * @param <T>
     *            parameter type
     * @param key
     *            a string containing the specified <code>key</code>
     * @return the binding <code>Object</code> stored with this key; if there are no keys, it will return defaultValue parameter
     * @see Action#getValue(String)
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(final String key) {
        return (T) super.getValue(key);
    }

    /**
     * Gets the <code>Object</code> associated with the specified key.
     *
     * @param <T>
     *            parameter type
     * @param key
     *            a string containing the specified <code>key</code>
     * @param defaultValue
     *            default value
     * @return the binding <code>Object</code> stored with this key; if there are no keys, it will return defaultValue parameter
     * @see Action#getValue(String)
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(final String key, final T defaultValue) {
        Object value = getParameter(key);
        return (T) (value != null ? value : defaultValue);
    }

    /**
     * Return source button (or menu item) state.
     *
     * @return {@link Boolean#TRUE} if button is selected, {@link Boolean#FALSE} if not and <code>null</code> if original event was not produced by button.
     */
    public Boolean isSourceSelected() {
        return getParameter(PARAMETER_SOURCE_SELECTED);
    }

    /**
     * Return selected combobox item.
     *
     * @param <T>
     *            selected item type
     * @return selected item or <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public <T> T getSelectedItem() {
        return (T) super.getValue(PARAMETER_ITEM_SELECTED);
    }

    /**
     * Adds a parameter to the action
     *
     * @param parameter
     *            action parameter to set
     */
    public void addParameter(final IActionParameter parameter) {
        m_actionParameters.put(parameter.getName(), parameter);
    }

    /**
     * Show action progress indicator
     */
    public void showProgress() {
        showProgress(LengthUtils.safeString(getDescription(), "Wait..."));
    }

    /**
     * Show action progress indicator
     *
     * @param text
     *            step text
     */
    public void showProgress(final String text) {
        // ProgressMonitor.getInstance().step(text);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     *            action event
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e) {
        setOriginalEvent(e);
        try {
            run();
        } finally {
            setOriginalEvent((ActionEvent) null);
        }
    }

    /**
     * Manual action execution.
     *
     * @param source
     *            action source
     */
    public void run(final Component source) {
        setOriginalEvent(source);
        try {
            run();
        } finally {
            setOriginalEvent((ActionEvent) null);
        }
    }

    /**
     *
     */
    protected void run() {
        try {
            setParameters();
            // ProgressMonitor.getInstance().begin(this);
            ActionDispatcher.dispatch(this);
        } catch (Throwable th) {
            th.printStackTrace();
        } finally {
            // ProgressMonitor.getInstance().end(this);
        }
    }

    /**
     * Installs the action to an action map of the target component using Action.NAME property and to an input map if target component using Action.NAME,
     * ActionEx.ACCELERATOR_KEY properties and given additinal accelerators.
     *
     * @param c
     *            the target component
     * @param additionalAccelerators
     *            an array of additional accelerators
     */
    public void installAction(final JComponent c, final KeyStroke... additionalAccelerators) {
        Object nameValue = this.getValue(Action.NAME);
        String name = nameValue != null ? nameValue.toString() : this.toString();
        ActionMap map = c.getActionMap();
        map.put(name, this);

        InputMap imap = getInputMap(c);
        KeyStroke mainAccelerator = this.getAccelerator();
        if (mainAccelerator != null) {
            imap.put(mainAccelerator, name);
        }

        if (LengthUtils.isNotEmpty(additionalAccelerators)) {
            for (KeyStroke additional : additionalAccelerators) {
                imap.put(additional, name);
            }
        }
    }

    /**
     * Uninstalls the action from action & input maps of the target component using Action.NAME property.
     *
     * @param c
     *            the target component
     */
    public void uninstallAction(final JComponent c) {
        Object nameValue = this.getValue(Action.NAME);
        String name = nameValue != null ? nameValue.toString() : this.toString();
        ActionMap map = c.getActionMap();

        if (map.get(name) == this) {
            map.remove(name);

            InputMap imap = getInputMap(c);
            KeyStroke[] ks = imap.allKeys();

            for (int i = 0; i < ks.length; i++) {
                if (name.equals(imap.get(ks[i]))) {
                    imap.remove(ks[i]);
                }
            }
        }
    }

    /**
     * Creates the menu item.
     *
     * @return an instance of the {@link JMenuItem} object
     */
    public JMenuItem createMenuItem() {
        JMenuItem mi = new JMenuItem(this);
        mi.setName(this.getId() + "MenuItem");
        return mi;
    }

    /**
     * Creates the check menu item.
     *
     * @return an instance of the {@link JCheckBoxMenuItem} object
     */
    public JCheckBoxMenuItem createCheckMenuItem() {
        JCheckBoxMenuItem mi = new JCheckBoxMenuItem(this);
        mi.setName(this.getId() + "CheckBoxMenuItem");
        return mi;
    }

    /**
     * Returns input map for the given component
     *
     * @param c
     *            the target component
     * @return an instance of the <code>javax.swing.InputMap</code> class.
     */
    private static InputMap getInputMap(final JComponent c) {
        if (c instanceof JMenuItem) {
            return c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        } else {
            return c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        }
    }

    /**
     * Sets parameter values to the action
     */
    private void setParameters() {
        for (Entry<String, IActionParameter> entry : m_actionParameters.entrySet()) {
            putValue(entry.getKey(), entry.getValue().getValue());
        }
    }

    /**
     * Retrieves name part from the action's text
     *
     * @param text
     *            action's text
     * @return action's name
     */
    private static String parseName(final String text) {
        int place = text.indexOf(ActionEx.MNEMONIC_DELIMER);

        if (place > -1) {
            return text.substring(0, place) + text.substring(place + 1, text.length());
        }

        return text;
    }

    /**
     * Retrieves menumonic part from the action's text
     *
     * @param text
     *            action's text
     * @return action's mnemonic
     */
    private static Integer parseMenumonic(final String text) {
        int place = text.indexOf(ActionEx.MNEMONIC_DELIMER);

        if ((place > -1) && ((place + 1) < text.length())) {
            String upper = text.substring(place + 1, place + 2);
            upper = upper.toUpperCase();
            return new Integer(upper.charAt(0));
        }

        return null;
    }
}
