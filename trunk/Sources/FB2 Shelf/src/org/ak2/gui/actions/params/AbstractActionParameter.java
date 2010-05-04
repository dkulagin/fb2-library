package org.ak2.gui.actions.params;

public abstract class AbstractActionParameter implements IActionParameter {

    private String m_name;

    /**
     * Constructor.
     *
     * @param name
     *            the name
     */
    protected AbstractActionParameter(final String name) {
        super();
        m_name = name;
    }

    /**
     * @return parameter name
     */
    public String getName() {
        return m_name;
    }
}
