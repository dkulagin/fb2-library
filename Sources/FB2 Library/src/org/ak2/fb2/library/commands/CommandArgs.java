package org.ak2.fb2.library.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.enums.EnumUtils;

public class CommandArgs {

    private Map<String, String> m_args = new LinkedHashMap<String, String>();

    public CommandArgs(final String... args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && (i + 1 < args.length)) {
                String name = args[i].substring(1);
                String value = args[i + 1];
                m_args.put(name, value);
            }
        }
    }

    public String getValue(final String name) {
        return m_args.get(name);
    }

    public String getValue(final String name, final String defaultValue) {
        return LengthUtils.safeString(m_args.get(name), defaultValue);
    }

    public <T extends Enum<T>> T getValue(final String name, final Class<T> valueClass, final T defaultValue) {
        String value = getValue(name);
        if (LengthUtils.isNotEmpty(value)) {
            return EnumUtils.valueOf(valueClass, value);
        }
        return defaultValue;
    }

    @Override
    public String toString() {
        return m_args.toString();
    }

}
