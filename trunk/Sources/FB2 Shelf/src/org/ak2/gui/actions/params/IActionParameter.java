package org.ak2.gui.actions.params;

public interface IActionParameter {
    /**
     * @return parameter name
     */
    public String getName();

    /**
     * Calculates a parameter value
     *
     * @return value
     */
    public Object getValue();
}
