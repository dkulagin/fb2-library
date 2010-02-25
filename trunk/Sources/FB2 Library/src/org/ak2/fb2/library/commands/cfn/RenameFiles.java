package org.ak2.fb2.library.commands.cfn;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.fb2.library.common.ProcessingException;
import org.ak2.fb2.library.common.ProcessingResult;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.collections.CountersMap;
import org.ak2.utils.files.FileScanner;
import org.ak2.utils.files.IFile;
import org.ak2.utils.files.IFileFilter;
import org.ak2.utils.jlog.JLogLevel;

/**
 * @author Alexander Kasatkin
 *
 */
public class RenameFiles extends AbstractCommand {

    private final Properties authors = new Properties();

    private final Properties series = new Properties();

    private final IRenameHelper helper;

    private final CountersMap<ProcessingResult> counters = new CountersMap<ProcessingResult>();

    public RenameFiles() {
        this(null);
    }

    public RenameFiles(final IRenameHelper helper) {
        super("cfn");
        try {
            authors.load(RenameFiles.class.getResourceAsStream("Authors.properties"));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        try {
            series.load(RenameFiles.class.getResourceAsStream("Series.properties"));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        this.helper = helper != null ? helper : new DefaultRenameHelper();
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        MSG_ARGS.log(this.getClass().getSimpleName(), args);

        // parsing parameters
        final String inputFolder = args.getValue(PARAM_INPUT);
        final String outputFolder = args.getValue(PARAM_OUTPUT);
        final OutputFormat outFormat = args.getValue(PARAM_OUTFORMAT, OutputFormat.class, OutputFormat.Zip);
        final OutputPath outPath = args.getValue(PARAM_OUTPATH, OutputPath.class, OutputPath.Standard);

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.", true);
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

        logBoldLine();
        MSG_INFO_VALUE.log("Processing input folder", inputFolder);
        MSG_INFO_VALUE.log("Writing output into    ", outputFolder);
        MSG_INFO_VALUE.log("Output book format     ", outFormat);
        MSG_INFO_VALUE.log("Output book path type  ", outPath);
        logBoldLine();

        final File inFile = new File(inputFolder);
        final File outFolder = new File(outputFolder);

        if (inFile.equals(outFolder)) {
            throw new BadCmdArguments("Input and output folders cannot be the same.");
        }
        outFolder.mkdirs();
        if (!outFolder.exists()) {
            throw new BadCmdArguments("Output folder is not exist");
        }

        execute(inFile, outFolder, outFormat, outPath);

        logBoldLine();
        printResults();
        logBoldLine();
    }

    public Collection<File> execute(final File inFile, final File outFile, final OutputFormat outFormat, final OutputPath outPath) {

        final Set<File> result = new LinkedHashSet<File>();

        FileScanner.enumerate(inFile, new IFileFilter() {
            @Override
            public boolean accept(final IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    try {
                        logLine(JLogLevel.DEBUG);
                        final File newFile = processFile(file, outFile, outFormat, outPath);
                        result.add(newFile);
                        counters.increment(ProcessingResult.CREATED);
                    } catch (final ProcessingException ex) {
                        final ProcessingResult pr = ex.getResult();
                        if (pr == ProcessingResult.FAILED) {
                            MSG_ERROR.log(ex, file.getFullName());
                        } else {
                            final File outFile = ex.getFile();
                            if (outFile != null) {
                                result.add(outFile);
                            }
                        }
                        counters.increment(pr);
                    } catch (final Throwable th) {
                        MSG_ERROR.log(th, file.getFullName());
                        counters.increment(ProcessingResult.FAILED);
                    }
                }
                return true;
            }

        }, new FileScanner.Options(true, true));

        return result;
    }

    public void printResults() {
        MSG_INFO_VALUE.log("Created new", counters.get(ProcessingResult.CREATED));
        MSG_INFO_VALUE.log("Duplicated ", counters.get(ProcessingResult.DUPLICATED));
        MSG_INFO_VALUE.log("Failed     ", counters.get(ProcessingResult.FAILED));
    }

    public File processFile(final IFile file, final File outFile, final OutputFormat outFormat, final OutputPath outPath) throws ProcessingException {
        MSG_DEBUG_VALUE.log("File        ", file.getFullName());
        try {
            final XmlContent content = new XmlContent(file);
            return createBookFile(content, outFile, outFormat, outPath, true);
        } catch (final IOException ex) {
            throw new ProcessingException(ex);
        }
    }

    public File createBookFile(final XmlContent content, final File outputFolder, final OutputFormat outFormat, final OutputPath outPath, final boolean showInfo)
            throws ProcessingException {
        try {
            final FictionBook book = new FictionBook(content);

            final Map<String, Object> properties = helper.getBookProperties(book);

            BookAuthor author = (BookAuthor) properties.get(IRenameHelper.AUTHOR);
            String bookName = (String) properties.get(IRenameHelper.BOOK_NAME);
            String seq = (String) properties.get(IRenameHelper.BOOK_SEQUENCE);
            String seqNo = (String) properties.get(IRenameHelper.BOOK_SEQUENCE_NO);

            helper.setBookProperties(book, properties);

            if (showInfo) {
                MSG_DEBUG_VALUE.log("Author      ", author);
                if (LengthUtils.isNotEmpty(seq)) {
                    MSG_DEBUG_VALUE.log("Sequence    ", seq);
                }
                if (LengthUtils.isNotEmpty(seqNo)) {
                    MSG_DEBUG_VALUE.log("SequenceNo  ", seqNo);
                }
                MSG_DEBUG_VALUE.log("Book name   ", bookName);
            }

            if (LengthUtils.isNotEmpty(seqNo)) {
                while (seqNo.length() < 3) {
                    seqNo = "0" + seqNo;
                }
                bookName = seqNo + ". " + bookName;
            }

            seq = fixName(seq);
            bookName = fixName(bookName);

            String authorName = authors.getProperty(author.getName(), author.getName());
            seq = series.getProperty(seq, seq);

            final File bookFolder = outPath.getFolder(outputFolder, authorName, seq);
            final String bookFileName = bookName + ".fb2";
            return outFormat.createFile(bookFolder, bookFileName, book);
        } catch (final ProcessingException ex) {
            throw ex;
        } catch (final Exception ex) {
            throw new ProcessingException(ex);
        }
    }

    private static String fixName(String name) {
        // name = name.replace(' ', '_');
        name = name.replace(':', '_');
        name = name.replace('\\', '_');
        name = name.replace('/', '_');
        name = name.replace('<', '_');
        name = name.replace('>', '_');
        name = name.replace('?', '_');
        name = name.replace('"', '_');

        name = name.replace('\u00ab', '_');
        name = name.replace('\u00bb', '_');

        return name;
    }
}
