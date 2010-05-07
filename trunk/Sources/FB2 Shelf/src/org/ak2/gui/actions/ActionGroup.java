package org.ak2.gui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;

public class ActionGroup implements PropertyChangeListener {

    private final Set<ActionEx> m_actions = new HashSet<ActionEx>();

    public ActionGroup(ActionEx... actions) {
        for (ActionEx action : actions) {
            add(action);
        }
    }

    public void add(final ActionEx action) {
        if (action != null && m_actions.add(action)) {
            action.setActionGroup(this);
            action.addPropertyChangeListener(this);
        }
    }

    public void remove(final ActionEx action) {
        if (action != null && m_actions.remove(action)) {
            action.removePropertyChangeListener(this);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
            if (Boolean.TRUE == evt.getNewValue()) {
                final Object source = evt.getSource();
                for (final ActionEx action : m_actions) {
                    if (action != source) {
                        action.putValue(Action.SELECTED_KEY, Boolean.FALSE);
                    }
                }
            }
        }
    }
}
