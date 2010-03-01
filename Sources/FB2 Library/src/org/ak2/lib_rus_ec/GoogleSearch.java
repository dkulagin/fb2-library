package org.ak2.lib_rus_ec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSearch {

    public static AuthorPage getAuthorPage(final String searchString) throws IOException, JSONException {
        byte[] bytes = searchString.getBytes("UTF8");
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append("%").append(Integer.toHexString(((int) b) & 0xFF).toUpperCase());
        }

        URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&hl=ru&q=" + buf.toString() + " +site:lib.rus.ec/a/");
        URLConnection connection = url.openConnection();

        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        JSONObject json = new JSONObject(builder.toString());
        JSONObject responseData = json == null ? json : json.getJSONObject("responseData");
        JSONArray results = responseData == null ? null : responseData.getJSONArray("results");

        if (results == null || results.length() == 0) {
            return null;
        }

        JSONObject result = results.getJSONObject(0);

        String name = result.getString("titleNoFormatting");
        int index = name.indexOf(" | Либрусек");
        if (index >= 0) {
            name = name.substring(0, index);
        }

        String link = result.getString("unescapedUrl");

        return new AuthorPage(name, new URL(link));
    }

}
