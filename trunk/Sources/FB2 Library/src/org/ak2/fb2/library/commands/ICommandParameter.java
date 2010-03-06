package org.ak2.fb2.library.commands;

public interface ICommandParameter {

    public String getName();
    
    public String getDescription();
    
    public Object getDefaultValue();
    
    public Object[] getValues();
}
