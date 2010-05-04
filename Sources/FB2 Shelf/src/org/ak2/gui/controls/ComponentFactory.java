package org.ak2.gui.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ak2.gui.actions.ActionEx;

public final class ComponentFactory
{
    /**
     * Constructor
     */
    private ComponentFactory()
    {

    }

    /**
     * Creates component label
     *
     * @param name current name
     * @return label{@link java.swing.JLabel}}
     */
    public static JLabel createLabel(final String name)
    {
        JLabel label = new JLabel();
        label.setName(name);
        return label;
    }

    /**
     * Creates glue component
     *
     * @param name current name
     * @return {@link java.awt.Component}
     */
    public static Component createGlue(final String name)
    {
        Component jGlue = Box.createGlue();
        jGlue.setName(name);
        return jGlue;
    }

    /**
     * Creates component label
     *
     * @param name current name
     * @param action current action
     * @return label{@link java.swing.JLabel}}
     */
    public static JLabel createLabel(final String name, final ActionEx action)
    {
        JLabel label = new JLabel(action.getText());
        label.setName(name);
        label.setIcon(action.getIcon());
        return label;
    }

    /**
     * Creates component panel
     *
     * @param name current name
     * @return panel {@link java.swing.JPanel}}
     */
    public static JPanel createPanel(final String name)
    {
        JPanel panel = new JPanel();
        panel.setName(name);
        return panel;
    }

    /**
     * Creates panel component
     *
     * @param name current name
     * @param layout current layout manager
     * @return panel {@link java.swing.JPanel}}
     */
    public static JPanel createPanel(final String name, final LayoutManager layout)
    {
        JPanel panel = new JPanel();
        panel.setName(name);
        panel.setLayout(layout);
        return panel;
    }

    /**
     * Creates icon button for the action
     *
     * @param action current action
     * @return an instance of {@link JButton} object
     */
    public static JButton createIconButton(final ActionEx action)
    {
        return createIconButton(0, action);
    }

    /**
     * Creates icon button for the action
     *
     * @param size preferred size
     * @param action current action
     * @return an instance of {@link JButton} object
     */
    public static JButton createIconButton(final int size, final ActionEx action)
    {
        JButton b = new JButton();
        b.setName(action.getId() + "Button");
        b.putClientProperty("hideActionText", true);
        b.setAction(action);
        if (size > 0)
        {
            Dimension dimension = new Dimension(size, size);
            b.setMinimumSize(dimension);
            b.setPreferredSize(dimension);
            b.setMaximumSize(dimension);
        }
        return b;
    }
}
