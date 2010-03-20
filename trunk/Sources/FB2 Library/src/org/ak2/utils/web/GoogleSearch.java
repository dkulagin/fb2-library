package org.ak2.utils.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.http.HttpContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSearch {

    private static final String GOOGLE_API_URL = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&hl=ru&q=";

    public static JSONObject searchFirst(final String searchString) throws IOException, JSONException {
        return searchFirst(searchString, null);
    }

    public static List<JSONObject> searchAll(final String searchString) throws IOException, JSONException {
        return searchAll(searchString, null);
    }

    public static JSONObject searchFirst(final String searchString, final String site) throws IOException, JSONException {
        final JSONArray results = getSearchResults(searchString, site);

        if (results == null || results.length() == 0) {
            return null;
        }

        final JSONObject result = results.getJSONObject(0);
        return result;
    }

    public static List<JSONObject> searchAll(final String searchString, final String site) throws IOException, JSONException {
        final JSONArray results = getSearchResults(searchString, site);
        if (results == null) {
            return Collections.emptyList();
        }

        final int length = results.length();
        if (length == 0) {
            return Collections.emptyList();
        }

        final List<JSONObject> list = new ArrayList<JSONObject>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject result = results.getJSONObject(i);
            list.add(result);
        }

        return list;
    }

    private static JSONArray getSearchResults(final String searchString, final String site) throws UnsupportedEncodingException, MalformedURLException,
            IOException, JSONException {
        final URL url = getSearchRequest(searchString, site);

        IWebContent content = new HttpContent(url);
        final String text = StreamUtils.getText(content.getReader());

        final JSONObject json = new JSONObject(text);
        final JSONObject responseData = json == null ? json : json.getJSONObject("responseData");
        final JSONArray results = responseData == null ? null : responseData.getJSONArray("results");
        return results;
    }

    static URL getSearchRequest(final String searchString, final String site) throws UnsupportedEncodingException, MalformedURLException {
        final String escaped = escape(searchString);
        final URL url = new URL(GOOGLE_API_URL + escaped + (LengthUtils.isNotEmpty(site) ? "+site:" + site : ""));
        return url;
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
}
