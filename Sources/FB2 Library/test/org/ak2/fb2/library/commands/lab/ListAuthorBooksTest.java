package org.ak2.fb2.library.commands.lab;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.junit.Test;

public class ListAuthorBooksTest {

    @Test
    public void scheglov() throws LibraryException {
        ListAuthorBooks cmd = new ListAuthorBooks();
        CommandArgs args = new CommandArgs("-author", "Щеглов");
        cmd.execute(args);
    }

    @Test
    public void zvjagintsev() throws LibraryException {
        ListAuthorBooks cmd = new ListAuthorBooks();
        CommandArgs args = new CommandArgs("-author", "Звягинцев");
        cmd.execute(args);
    }
}
