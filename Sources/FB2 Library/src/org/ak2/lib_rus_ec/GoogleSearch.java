package org.ak2.lib_rus_ec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSearch {

    public static AuthorPage getAuthorPage(final String searchString) throws IOException, JSONException {
        final JSONArray results = getSearchResults(searchString);

        if (results == null || results.length() == 0) {
            return null;
        }

        final JSONObject result = results.getJSONObject(0);

        return getAuthorPage(result);
    }

    public static List<AuthorPage> getAuthorPages(final String searchString) throws IOException, JSONException {
        final JSONArray results = getSearchResults(searchString);
        if (results == null) {
            return Collections.emptyList();
        }

        final int length = results.length();
        if (length == 0) {
            return Collections.emptyList();
        }

        final List<AuthorPage> list = new ArrayList<AuthorPage>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject result = results.getJSONObject(i);
            list.add(getAuthorPage(result));
        }

        return list;
    }

    private static JSONArray getSearchResults(final String searchString) throws UnsupportedEncodingException, MalformedURLException, IOException, JSONException {
        final String escaped = escape(searchString);
        final URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&hl=ru&q=" + escaped + "+site:lib.rus.ec/a/");
        final URLConnection connection = url.openConnection();

        String line;
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        final JSONObject json = new JSONObject(builder.toString());
        final JSONObject responseData = json == null ? json : json.getJSONObject("responseData");
        final JSONArray results = responseData == null ? null : responseData.getJSONArray("results");
        return results;
    }

    private static String escape(final String searchString) throws UnsupportedEncodingException {
        final byte[] bytes = searchString.getBytes("UTF8");
        final StringBuilder buf = new StringBuilder();
        for (final byte b : bytes) {
            buf.append("%").append(Integer.toHexString((b) & 0xFF).toUpperCase());
        }

        final String string = buf.toString();
        return string;
    }

    private static AuthorPage getAuthorPage(final JSONObject result) throws JSONException, MalformedURLException {
        String name = result.getString("titleNoFormatting");
        final int index = name.indexOf(" | Либрусек");
        if (index >= 0) {
            name = name.substring(0, index);
        }

        final String link = result.getString("unescapedUrl");

        return new AuthorPage(name, new URL(link));
    }

}
