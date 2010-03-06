package org.ak2.fb2.library.commands.dnb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommandParameter;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.commands.parameters.BaseParameter;
import org.ak2.fb2.library.commands.parameters.EnumParameter;
import org.ak2.fb2.library.commands.parameters.FileSystemParameter;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.fb2.library.common.ProcessingException;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.lib_rus_ec.AuthorPage;
import org.ak2.lib_rus_ec.BookPage;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.jlog.JLog;

public class DownloadBooks extends AbstractCommand {

    private static final ICommandParameter[] PARAMS = {
    /** -author-id <author-id> - author id on Lib.Rus.Ec */
    new BaseParameter("author-id", "author id on Lib.Rus.Ec", ""),
    /** -author-last-name <author-last-name> - author last name */
    new BaseParameter("author-last-name", "author id on Lib.Rus.Ec", ""),
    /** -author-first-name <author-first-name> - author first name */
    new BaseParameter("author-first-name", "author first name", ""),
    /** -book-ids <book-ids> - books ids on Lib.Rus.Ec */
    new BaseParameter("book-ids", "books ids on Lib.Rus.Ec", ""),
    /** -output <target folder> - folder to store renamed book */
    new FileSystemParameter(PARAM_OUTPUT, "folder to store renamed book", true, false),
    /** -outpath <output path type> - output path type */
    new EnumParameter(PARAM_OUTPATH, "output path type", OutputPath.values(), OutputPath.Standard),
    /** -outformat <output book format> - output book format */
    new EnumParameter(PARAM_OUTFORMAT, "output book format", OutputFormat.values(), OutputFormat.Zip), };

    /**
     * Constructor.
     */
    public DownloadBooks() {
        super("dnb");
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
        final String authorId = args.getValue("author-id");
        final String authorLastName = args.getValue("author-last-name");
        final String authorFirstName = args.getValue("author-first-name");
        final String outputFolder = args.getValue(PARAM_OUTPUT);
        final OutputFormat outFormat = args.getValue(PARAM_OUTFORMAT, OutputFormat.class, OutputFormat.Zip);
        final OutputPath outPath = args.getValue(PARAM_OUTPATH, OutputPath.class, OutputPath.Standard);

        if (LengthUtils.isEmpty(authorId)) {
            throw new BadCmdArguments("Author id should be defined.", true);
        }
        if (LengthUtils.isEmpty(authorLastName)) {
            throw new BadCmdArguments("Author last name is missing.", true);
        }
        if (LengthUtils.isEmpty(outputFolder)) {
            throw new BadCmdArguments("Output folder is missing.", true);
        }
        if (outFormat == null) {
            throw new BadCmdArguments("Output format is wrong.", true);
        }
        if (outPath == null) {
            throw new BadCmdArguments("Output path type is wrong.", true);
        }

        final Set<String> bookIds = new HashSet<String>(Arrays.asList(LengthUtils.safeString(args.getValue("book-ids")).split(":")));
        for (Iterator<String> iterator = bookIds.iterator(); iterator.hasNext();) {
            String string = (String) iterator.next();
            if (LengthUtils.isEmpty(string)) {
                iterator.remove();
            }
        }

        final BookAuthor author = new BookAuthor(authorFirstName, authorLastName);

        logBoldLine();
        MSG_INFO_VALUE.log("Author                 ", author);
        MSG_INFO_VALUE.log("Author id              ", authorId);
        MSG_INFO_VALUE.log("Book   ids             ", bookIds);
        MSG_INFO_VALUE.log("Writing output into    ", outputFolder);
        MSG_INFO_VALUE.log("Output book format     ", outFormat);
        MSG_INFO_VALUE.log("Output book path type  ", outPath);
        logBoldLine();

        final File outFolder = new File(outputFolder);

        outFolder.mkdirs();
        if (!outFolder.exists()) {
            throw new BadCmdArguments("Output folder is not exist");
        }

        final RenameFiles cmd = new RenameFiles();
        try {
            final List<BookPage> booksList = getBooksList(author, authorId, bookIds);
            for (final BookPage bookPage : booksList) {
                JLog.info("Download book: " + bookPage);
                final XmlContent content = bookPage.getContent();
                try {
                    cmd.createBookFile(content, outFolder, outFormat, outPath, false);
                } catch (ProcessingException ex) {
                    File file = new File(outFolder, bookPage.getAuthorPage().getAuthor() + ". " + bookPage.getName() + ".xml");
                    Writer out = new OutputStreamWriter(new FileOutputStream(file), content.getEncoding());
                    out.append(content.getContent());
                    out.close();
                }
            }
        } catch (final Exception ex) {
            throw new LibraryException(ex);
        }
    }

    protected List<BookPage> getBooksList(final BookAuthor author, final String authorId, final Set<String> bookIds) throws IOException {
        final AuthorPage authorPage = new AuthorPage(author, authorId);
        final List<BookPage> books = authorPage.getBooks();
        if (LengthUtils.isNotEmpty(bookIds)) {
            for (final Iterator<BookPage> iter = books.iterator(); iter.hasNext();) {
                final BookPage bookPage = iter.next();
                if (!bookIds.contains(bookPage.getId())) {
                    iter.remove();
                }
            }
        }

        return books;
    }

}
