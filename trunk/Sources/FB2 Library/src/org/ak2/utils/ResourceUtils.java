package org.ak2.utils;

public class ResourceUtils {

    public static String getPackageResource(final String prefix, final Class<?> clazz, final String suffix) {
        StringBuilder buf = new StringBuilder(prefix);
        buf.append(clazz.getPackage().getName().replace('.', '/'));
        buf.append(suffix);
        return buf.toString();
    }
}
