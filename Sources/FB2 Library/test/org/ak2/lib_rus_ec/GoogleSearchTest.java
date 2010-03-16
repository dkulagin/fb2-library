package org.ak2.lib_rus_ec;

import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;

public class GoogleSearchTest {

    @BeforeClass
    public static void init() throws URISyntaxException {
        //System.setProperty("java.net.useSystemProxies", "true");
        //System.setProperty("http.proxyPort", "3128");
        //System.setProperty("http.proxyHost", "proxy.reksoft.ru");
    }

    @Test
    public void test() throws Exception {
        AuthorPage authorPage = LibRusEc.getAuthorPage("Кирилл Еськов");
        System.out.println(authorPage);
    }


}
