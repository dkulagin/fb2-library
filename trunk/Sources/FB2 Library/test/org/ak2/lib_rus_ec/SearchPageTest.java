package org.ak2.lib_rus_ec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.fb2.library.common.ProcessingException;
import org.ak2.utils.files.FileScanner;
import org.ak2.utils.files.IFile;
import org.ak2.utils.files.IFileFilter;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchPageTest {

    private static File folder;

    @BeforeClass
    public static void init() {
        System.setProperty("http.proxyHost", "proxy.reksoft.ru");
        System.setProperty("http.proxyPort", "3128");

        folder = new File("output");
        folder.mkdirs();

        FileScanner.enumerate(folder, new IFileFilter() {
            @Override
            public boolean accept(IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    file.getRealFile().delete();
                }
                return false;
            }
        }, new FileScanner.Options(true, false));
    }

    @Test
    public void test() throws IOException, ProcessingException, JSONException {
        AuthorPage a = LibRusEc.getAuthorPage("Кирилл Еськов");

        List<BookPage> books = a.getBooks();
        for (BookPage bookPage : books) {
            System.out.println(bookPage);
        }

        BookPage bookPage = books.get(8);
        XmlContent content = bookPage.getContent();
        RenameFiles cmd = new RenameFiles();
        try {
            cmd.createBookFile(content, folder, OutputFormat.Fb2, OutputPath.Standard, false);
        } catch (ProcessingException ex) {
            String fileName = bookPage.getAuthorPage().getAuthor() + ". " + bookPage.getName() + ".xml";
            content.saveToFile(new File(fileName));
            throw ex;
        }
    }

}
