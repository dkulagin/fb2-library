package org.ak2.gui.actions.params;

public class Constant extends AbstractActionParameter {

    private Object m_value;

    /**
     * Constructor.
     *
     * @param name
     *            the parameter name
     * @param value
     *            the parameter value
     */
    public Constant(final String name, final Object value) {
        super(name);
        m_value = value;
    }

    /**
     * @return value
     */
    public Object getValue() {
        return m_value;
    }

}
