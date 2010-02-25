package org.ak2.lib_rus_ec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.utils.LengthUtils;

public class AuthorPage {

    private static final String PATTERN = "<h1 class=\"title\">([^<]+)</h1>|<a name=(\\w+)><a class=genre href=\\/g\\/\\w+><h9>([^<]+)<\\/h9>|<a href=\\/s\\/\\d+><h8>([^<]+)</h8>|<input type=checkbox  id='[\\w-]+' name=\\d+>\\s+-\\s+((\\d+)\\.\\s*)?<a href=(\\/b\\/\\d+)>([^<]+)<\\/a>";

    private final URL m_authorUrl;

    private String m_name;

    public AuthorPage(final URL authorUrl) {
        m_authorUrl = authorUrl;
    }

    public AuthorPage(String name, URL authorUrl) {
        super();
        m_name = name;
        m_authorUrl = authorUrl;
    }

    public List<BookPage> getBooks() throws IOException {
        final List<BookPage> list = new ArrayList<BookPage>();

        final URLConnection conn = m_authorUrl.openConnection();
        final StringBuilder buf = new StringBuilder();
        final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
        for (String s = in.readLine(); s != null; s = in.readLine()) {
            buf.append(s).append('\n');
        }

        String currentGenre = null;
        String currentSequence = null;

        final Pattern p = Pattern.compile(PATTERN, Pattern.DOTALL);
        final Matcher m = p.matcher(buf);
        for (int start = 0; m.find(start); start = m.end()) {
            if (LengthUtils.isNotEmpty(m.group(1))) {
                m_name = m.group(1);
            } else if (LengthUtils.isNotEmpty(m.group(2))) {
                currentGenre = m.group(2);
            } else if (LengthUtils.isNotEmpty(m.group(4))) {
                currentSequence = m.group(4);
            } else {
                final String seqNo = m.group(6);
                final String link = m.group(7);
                final String name = m.group(8);
                final BookPage book = new BookPage(this, name, currentGenre, currentSequence, seqNo, link);
                list.add(book);
            }

        }
        return list;
    }

    public URL getAuthorUrl() {
        return m_authorUrl;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    @Override
    public String toString() {
        return "AuthorPage [m_name=" + m_name + ", m_authorUrl=" + m_authorUrl + "]";
    }

}
