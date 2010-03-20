package org.ak2.lib_rus_ec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.RegexpUtils;
import org.ak2.utils.web.GoogleSearch;
import org.json.JSONException;
import org.json.JSONObject;

public class LibRusEc {

    public static final String SITE = "lib.rus.ec";

    public static final String AUTHOR_PATH = "/a/";

    private static final Pattern AUTHOR_LINK_PATTERN = Pattern.compile(RegexpUtils.getNameRegexp("http://" + SITE + AUTHOR_PATH) + "\\d+");

    public static String getId(final String link) {
        final int index = link.lastIndexOf("/");
        return index < 0 ? link : link.substring(index + 1);
    }

    public static String getId(final URL link) {
        return getId(link.toExternalForm());
    }

    public static AuthorPage getAuthorPage(final String searchString) throws IOException, JSONException {
        final JSONObject result = GoogleSearch.searchFirst(searchString, SITE + AUTHOR_PATH);

        if (result == null) {
            return null;
        }

        return getAuthorPage(result);
    }

    public static List<AuthorPage> getAuthorPages(final String searchString) throws IOException, JSONException {
        final List<JSONObject> results = GoogleSearch.searchAll(searchString, SITE + AUTHOR_PATH);

        if (LengthUtils.isEmpty(results)) {
            return Collections.emptyList();
        }

        final List<AuthorPage> list = new ArrayList<AuthorPage>(results.size());
        for (final JSONObject result : results) {
            AuthorPage authorPage = getAuthorPage(result);
            if (authorPage != null) {
                list.add(authorPage);
            }
        }

        return list;
    }

    private static AuthorPage getAuthorPage(final JSONObject result) throws JSONException, MalformedURLException {
        final String link = result.getString("unescapedUrl");
        if (AUTHOR_LINK_PATTERN.matcher(link).matches()) {
            String name = result.getString("titleNoFormatting");
            final int index = name.indexOf(" | Либрусек");
            if (index >= 0) {
                name = name.substring(0, index);
            }

            return new AuthorPage(name, new URL(link));
        }
        return null;
    }
}
