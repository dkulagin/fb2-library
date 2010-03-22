package org.ak2.fb2.library.commands.dnb;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.lib_rus_ec.BookPageTransformer;
import org.ak2.utils.web.cache.CacheManager;
import org.ak2.utils.web.cache.CachedContent;
import org.junit.Test;


public class DownloadBooksTest {

    @Test
    public void scheglov() throws LibraryException {
        BookPageTransformer t = new BookPageTransformer();

        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }

        DownloadBooks cmd = new DownloadBooks(t);
        CommandArgs args = new CommandArgs("dnb", "-author-id", "10877", "-author-last-name", "Щеглов", "-author-first-name", "Сергей", "-output", "./output", "-outpath", "Standard", "-outformat", "Zip");

        cmd.execute(args);
    }

    @Test
    public void zvjagintsev() throws LibraryException {
        BookPageTransformer t = new BookPageTransformer();

        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }

        DownloadBooks cmd = new DownloadBooks(t);
        CommandArgs args = new CommandArgs("dnb", "-author-id", "13878", "-author-last-name", "Звягинцев", "-author-first-name", "Василий", "-output", "./output", "-outpath", "Standard", "-outformat", "Zip");

        cmd.execute(args);
    }
    
    @Test
    public void fomichev() throws LibraryException {
        BookPageTransformer t = new BookPageTransformer();

        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }

        DownloadBooks cmd = new DownloadBooks(t);
        CommandArgs args = new CommandArgs("dnb", "-author-id", "29166", "-author-last-name", "Фомичев", "-author-first-name", "Алексей", "-output", "./output", "-outpath", "Standard", "-outformat", "Zip");

        cmd.execute(args);
    }
}
