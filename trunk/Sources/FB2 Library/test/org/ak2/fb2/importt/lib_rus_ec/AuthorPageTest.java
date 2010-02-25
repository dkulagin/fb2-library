package org.ak2.fb2.importt.lib_rus_ec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.fb2.library.common.ProcessingException;
import org.ak2.lib_rus_ec.AuthorPage;
import org.ak2.lib_rus_ec.BookPage;
import org.ak2.utils.files.FileScanner;
import org.ak2.utils.files.IFile;
import org.ak2.utils.files.IFileFilter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AuthorPageTest {

    @BeforeClass
    public static void init() {
        // System.setProperty("http.proxyHost", "proxy.reksoft.ru");
        // System.setProperty("http.proxyPort", "3128");

        FileScanner.enumerate(new File("."), new IFileFilter() {
            @Override
            public boolean accept(IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    file.getRealFile().delete();
                }
                return false;
            }
        }, null);
    }

    @Test
    public void test() throws IOException, ProcessingException {
        URL url = new URL("http://lib.rus.ec/a/5285");

        AuthorPage a = new AuthorPage(url);

        List<BookPage> books = a.getBooks();
        System.out.println(books);

        BookPage bookPage = books.get(3);
        
        XmlContent content = bookPage.getContent();

        RenameFiles cmd = new RenameFiles();

        cmd.createBookFile(content, new File("."), OutputFormat.Fb2, OutputPath.Simple, false);
    }

    //@Test
    public void testBook() throws IOException, ProcessingException {
        BookPage bookPage = new BookPage(new AuthorPage("author", null), "book", "genre", "sequence", "1", null);

        File f = new File("/home/whippet/Work/0000.My/FictionBook/Books/Lib.Rus.Ec/read.html");

        Assert.assertEquals(true, f.exists());

        XmlContent content = bookPage.getContent(new FileInputStream(f), "UTF8");

        RenameFiles cmd = new RenameFiles();

        cmd.createBookFile(content, new File("."), OutputFormat.Fb2, OutputPath.Simple, false);
    }

    //@Test
    public void testPoem() throws IOException, ProcessingException {
        BookPage bookPage = new BookPage(new AuthorPage("author", null), "poem", "genre", null, null, null);

        File f = new File("poem.html");

        Assert.assertEquals(true, f.exists());

        XmlContent content = bookPage.getContent(new FileInputStream(f), "UTF8");

        RenameFiles cmd = new RenameFiles();

        cmd.createBookFile(content, new File("."), OutputFormat.Fb2, OutputPath.Simple, false);
    }

}
