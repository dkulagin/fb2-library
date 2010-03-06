package org.ak2.lib_rus_ec;

import java.net.URL;

public class LibRusEc {

    public static final String SITE = "lib.rus.ec";

    public static final String AUTHOR_PATH = "/a/";

    public static String getId(String link) {
        final int index = link.lastIndexOf("/");
        return index < 0 ? link : link.substring(index + 1);
    }
    
    public static String getId(URL link) {
        return getId(link.toExternalForm());
    }
}
