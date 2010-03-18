package org.ak2.fb2.library.commands.dnb;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.junit.Test;


public class DownloadBooksTest {

    @Test
    public void test() throws LibraryException {
        DownloadBooks cmd = new DownloadBooks();
        CommandArgs args = new CommandArgs("dnb", "-author-id", "10877", "-author-last-name", "Щеглов", "-author-first-name", "Сергей", "-output", "./output", "-outpath", "Standard", "-outformat", "Zip");

        cmd.execute(args);
    }
}
