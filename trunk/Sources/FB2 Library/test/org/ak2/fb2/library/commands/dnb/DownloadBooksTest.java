package org.ak2.fb2.library.commands.dnb;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.lib_rus_ec.BookPageTransformer;
import org.ak2.utils.web.cache.CacheManager;
import org.ak2.utils.web.cache.CachedContent;
import org.junit.Test;


public class DownloadBooksTest {

    @Test
    public void test() throws LibraryException {
        BookPageTransformer t = new BookPageTransformer();

        // t.setFixNotes(false);
        // t.setFixTitles(false);
        // t.setFixDivs(false);
        // t.setFixBlockquotes(false);
        // t.setFixImages(false);
        // t.setFixTags(false);
        // t.setFixSections(false);
        // t.setLoadImages(false);

        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }

        DownloadBooks cmd = new DownloadBooks(t);
        CommandArgs args = new CommandArgs("dnb", "-author-id", "10877", "-author-last-name", "Щеглов", "-author-first-name", "Сергей", "-output", "./output", "-outpath", "Standard", "-outformat", "Zip");

        cmd.execute(args);
    }
}
