package org.ak2.gui.models.table.impl;

import java.lang.reflect.Method;

import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

/**
 * This class retrieves a value of the defined java bean's property.
 */
public class BeanPropertyAdapter implements ITableColumnAdapter {
    private static final JLogMessage BEAN_PROPERTY_ADAPTER_ERROR = new JLogMessage(JLogLevel.ERROR, "No getter method for bean property: {0} {1}");

    private Method[] fieldGetterMethods;

    private Class<?> fieldCellClass;

    /**
     * Contsructor.
     * 
     * @param entityClass
     *            the class of entity
     * @param propertyName
     *            the bean property name
     */
    public BeanPropertyAdapter(final Class<?> entityClass, final String propertyName) {
        String[] names = propertyName.split("\\.");
        fieldGetterMethods = new Method[LengthUtils.length(names)];

        Class<?> currentClass = entityClass;
        for (int i = 0, n = LengthUtils.length(fieldGetterMethods); i < n; i++) {
            String methodName = "get" + names[i];
            try {
                fieldGetterMethods[i] = currentClass.getMethod(methodName);
            } catch (Exception e) {
                methodName = "is" + names[i];
                try {
                    fieldGetterMethods[i] = currentClass.getMethod(methodName);
                } catch (Exception ex) {
                    fieldGetterMethods = null;
                    BEAN_PROPERTY_ADAPTER_ERROR.log(currentClass, propertyName);
                }
            }
            fieldCellClass = fieldGetterMethods != null ? fieldGetterMethods[i].getReturnType() : String.class;
            currentClass = fieldCellClass;
        }
    }

    /**
     * Returns the class of cell value.
     * 
     * @return a <code>Class</code> object.
     * @see ITableColumnAdapter#getCellClass()
     */
    public Class<?> getCellClass() {
        return fieldCellClass;
    }

    /**
     * Retrieves cell value from the given entity.
     * 
     * @param model
     *            the source model
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     * @param entity
     *            the entity
     * @return a <code>Object</code> object.
     * @see ITableColumnAdapter#getCellValue(ITableModel, int, int, java.lang.Object)
     */
    public Object getCellValue(final ITableModel<?, ?> model, final int rowIndex, final int columnIndex, final Object entity) {
        Object res = null;
        Object obj = entity;

        for (int i = 0, n = LengthUtils.length(fieldGetterMethods); i < n; i++) {
            if (fieldGetterMethods[i] == null) {
                return res;
            }
            if (obj == null || !fieldGetterMethods[i].getDeclaringClass().isInstance(obj)) {
                return LengthUtils.safeString();
            }
            try {
                res = fieldGetterMethods[i].invoke(obj);
            } catch (Throwable e) {
                return LengthUtils.safeString();
            }
            obj = res;
        }
        return res;
    }

}
