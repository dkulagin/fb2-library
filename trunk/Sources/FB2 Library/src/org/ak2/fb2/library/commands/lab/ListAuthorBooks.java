package org.ak2.fb2.library.commands.lab;

import java.util.List;

import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommandParameter;
import org.ak2.fb2.library.commands.parameters.BaseParameter;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.lib_rus_ec.AuthorPage;
import org.ak2.lib_rus_ec.BookPage;
import org.ak2.lib_rus_ec.LibRusEc;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.jlog.JLog;

public class ListAuthorBooks extends AbstractCommand {

    private static final ICommandParameter[] PARAMS = {
    /** -author  <author> - author last and/or first names */
    new BaseParameter("author", "author last and/or first names", ""), };

    /**
     * Constructor.
     */
    public ListAuthorBooks() {
        super("lab");
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommand#getParameters()
     */
    @Override
    public ICommandParameter[] getParameters() {
        return PARAMS;
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommand#execute(org.ak2.fb2.library.commands.CommandArgs)
     */
    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        final String author = args.getValue("author");
        if (LengthUtils.isEmpty(author)) {
            throw new BadCmdArguments("Author name is missing.", true);
        }
        try {
            final List<AuthorPage> authorPages = LibRusEc.getAuthorPages(author);
            for (final AuthorPage authorPage : authorPages) {
                JLog.info(authorPage.toString());
                List<BookPage> books = authorPage.getBooks();
                for (BookPage bookPage : books) {
                    JLog.info("\t" + bookPage);
                }
            }
        } catch (final Exception ex) {
            throw new LibraryException(ex);
        }
    }
}
