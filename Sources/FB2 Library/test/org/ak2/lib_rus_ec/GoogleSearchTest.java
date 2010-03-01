package org.ak2.lib_rus_ec;

import org.junit.BeforeClass;
import org.junit.Test;

public class GoogleSearchTest {

    @BeforeClass
    public static void init() {
        System.setProperty("http.proxyHost", "proxy.reksoft.ru");
        System.setProperty("http.proxyPort", "3128");
    }

    @Test
    public void test() throws Exception {
        AuthorPage authorPage = GoogleSearch.getAuthorPage("Кирилл Еськов");
        System.out.println(authorPage);
    }
}
