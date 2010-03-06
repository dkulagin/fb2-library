package org.ak2.lib_rus_ec;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.ak2.fb2.library.book.XmlContent;
import org.junit.Test;

public class TestHtml {

    @Test
    public void test() throws IOException {
        BookPage p = new BookPage(null, "name", "genre", "", "", "");

        XmlContent content = p.getContent(new FileInputStream("output/104324.htm"), "utf-8");

        String fileName = "output/104324.xml";
        Writer out = new OutputStreamWriter(new FileOutputStream(fileName), content.getEncoding());
        out.append(content.getContent());
        out.close();

    }

}
