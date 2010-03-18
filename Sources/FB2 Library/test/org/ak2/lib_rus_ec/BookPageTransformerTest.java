package org.ak2.lib_rus_ec;

import junit.framework.Assert;

import org.junit.Test;

public class BookPageTransformerTest {

    private static final String TEXT = "<title><p>Щеглов Сергей\n<p>Восход на Аиде\n</title>\n";

    @Test
    public void fixTitleParagraphs() {
        BookPageTransformer t = new BookPageTransformer();
        StringBuilder buf = new StringBuilder(TEXT);
        t.fixTitleParagraphs(buf);

        Assert.assertEquals("<title><p>Щеглов Сергей</p>\n<p>Восход на Аиде</p>\n</title>\n", buf.toString());
    }
}
