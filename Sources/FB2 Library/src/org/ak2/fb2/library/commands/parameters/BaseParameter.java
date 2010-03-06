package org.ak2.fb2.library.commands.parameters;

import org.ak2.fb2.library.commands.ICommandParameter;

public class BaseParameter implements ICommandParameter {

    private final String m_name;

    private final String m_desc;

    private final Object m_defValue;

    public BaseParameter(String name, String desc, Object defValue) {
        m_name = name;
        m_desc = desc;
        m_defValue = defValue;
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommandParameter#getName()
     */
    @Override
    public String getName() {
        return m_name;
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommandParameter#getDescription()
     */
    @Override
    public String getDescription() {
        return m_desc;
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommandParameter#getValues()
     */
    @Override
    public Object[] getValues() {
        return null;
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommandParameter#getDefaultValue()
     */
    @Override
    public Object getDefaultValue() {
        return m_defValue;
    }
}
