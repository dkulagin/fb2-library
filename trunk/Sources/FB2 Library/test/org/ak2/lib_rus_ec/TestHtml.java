package org.ak2.lib_rus_ec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.ak2.fb2.library.book.XmlContent;
import org.junit.Test;

public class TestHtml {

    @Test
    public void test() throws IOException {
        BookPage p = new BookPage(null, "name", "genre", "", "", "");

        XmlContent content = p.getContent(new FileInputStream("output/104324.htm"), "utf-8");

        String fileName = "output/104324.xml";
        content.saveToFile(new File(fileName));
    }
}
