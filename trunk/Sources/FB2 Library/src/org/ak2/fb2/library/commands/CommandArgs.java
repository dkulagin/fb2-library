package org.ak2.fb2.library.commands;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.enums.EnumUtils;
import org.ak2.utils.nls.NlsMessage;

public class CommandArgs {

    private static final NlsMessage MSG_LOCATION_NOT_EXIST = new NlsMessage("The location {0} does not exist");

    private static final NlsMessage MSG_BAD_VALUE = new NlsMessage("The following argument could not be parsed: {0}={1}");

    private final Map<String, String> m_args = new LinkedHashMap<String, String>();

    public CommandArgs(final String... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && (i + 1 < args.length)) {
                final String name = args[i].substring(1);
                final String value = args[i + 1];
                if (!value.startsWith("-")) {
                    m_args.put(name, value);
                }
            }
        }
    }

    public void setValue(final String name, final String value) {
        m_args.put(name, value);
    }

    public String getValue(final String name) {
        return m_args.get(name);
    }

    public String getValue(final String name, final String defaultValue) {
        return LengthUtils.safeString(m_args.get(name), defaultValue);
    }

    public boolean getValue(final String name, final boolean defaultValue) {
        String val = m_args.get(name);
        if (LengthUtils.isEmpty(val)) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(val) || "yes".equalsIgnoreCase(val);
    }

    public int getValue(final String name, final int defaultValue) throws BadCmdArguments {
        String val = getValue(name);
        if (LengthUtils.isEmpty(val)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            throw new BadCmdArguments(MSG_BAD_VALUE.bind(name, val));
        }
    }

    public <T extends Enum<T>> T getValue(final String name, final Class<T> valueClass, final T defaultValue) {
        final String value = getValue(name);
        if (LengthUtils.isNotEmpty(value)) {
            return EnumUtils.valueOf(valueClass, value);
        }
        return defaultValue;
    }

    public static List<File> getLocations(final String paths) throws BadCmdArguments {
        final List<File> locations = new LinkedList<File>();
        final String[] splitted = paths.split(File.pathSeparator);
        for (final String s : splitted) {
            if (LengthUtils.isNotEmpty(s)) {
                final File loc = new File(s);
                if (loc.exists()) {
                    locations.add(loc);
                } else {
                    throw new BadCmdArguments(MSG_LOCATION_NOT_EXIST.bind(s));
                }
            }
        }
        return locations;
    }

    public Collection<String> getArgNames() {
        return m_args.keySet();
    }
    @Override
    public String toString() {
        return m_args.toString();
    }

}
