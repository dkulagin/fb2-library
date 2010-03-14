package org.ak2.utils.web;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import org.ak2.utils.web.http.HttpContent;

public class Web {

    public static IWebContent get(final URL url) throws IOException {
        return new HttpContent(url);
    }

    public static IWebContent get(final URL url, Proxy proxy) throws IOException {
        return new HttpContent(url, proxy);
    }
}
