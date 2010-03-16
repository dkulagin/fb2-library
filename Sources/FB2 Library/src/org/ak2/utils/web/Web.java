package org.ak2.utils.web;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

import org.ak2.utils.web.cache.CacheManager;
import org.ak2.utils.web.http.HttpContent;

public final class Web {

    private Web() {
    }

    public static IWebContent get(final URL url) throws IOException {
        return get(url, Proxy.NO_PROXY);
    }

    public static IWebContent get(final URL url, Proxy proxy) throws IOException {
        IWebContent content = CacheManager.getInstance().get(url);
        if (content == null) {
            content = new HttpContent(url, proxy);
            content = CacheManager.getInstance().set(content);
        }
        return content;
    }
}
