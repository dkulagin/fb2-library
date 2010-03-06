package org.ak2.fb2.library.commands.parameters;


public class EnumParameter extends BaseParameter {

    private final Enum<?>[] m_values;
    
    public EnumParameter(String name, String desc, Enum<?>[] values, Enum<?> defValue) {
        super(name, desc, defValue);
        m_values = values;
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommandParameter#getValues()
     */
    @Override
    public Object[] getValues() {
        return m_values;
    }

}
