package org.ak2.lib_rus_ec;

import java.net.MalformedURLException;
import java.net.URL;

public class BookImage {

    private final BookPage m_bookPage;
    private final String m_id;
    private final URL m_link;

    public BookImage(final BookPage bookPage, final String link) throws MalformedURLException {
        super();
        m_bookPage = bookPage;

        URL authorUrl = bookPage.getAuthorPage().getAuthorUrl();
        m_link = new URL(authorUrl.getProtocol(), authorUrl.getHost(), link);

        int index = link.lastIndexOf("/");
        this.m_id = index < 0 ? link : link.substring(index+1);
    }

    public BookPage getBookPage() {
        return m_bookPage;
    }

    public String getId() {
        return m_id;
    }

    public URL getLink() {
        return m_link;
    }

}
