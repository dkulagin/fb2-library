package org.ak2.fb2.shelf.catalog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.ak2.utils.LengthUtils;

public class ShelfCatalogProvider {

    private static File s_catalog = new File("catalog.xml");

    private static final Map<String, String> s_locations = new HashMap<String, String>();

    public static boolean setCatalogFile(File catalog) {
        if (catalog != null && catalog.exists()) {
            s_catalog = catalog;
            return true;
        }
        return false;
    }

    public static ShelfCatalog getCatalog() {
        return new ShelfCatalog(s_catalog);
    }

    public static void setLocationMapping(final String oldLocation, final String newLocation) {
        s_locations.put(oldLocation, newLocation);
    }

    public static String getLocationMapping(final String path) {
        String normalized = BookInfo.normalize(path);
        return LengthUtils.safeString(s_locations.get(normalized), normalized);
    }
}
