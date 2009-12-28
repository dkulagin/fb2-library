package org.ak2.fb2.library.commands.cfn;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
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

/**
 * @author Alexander Kasatkin
 *
 */
public class RenameFiles implements ICommand {

    private final Properties authors = new Properties();

    private final Properties series = new Properties();

    private final IRenameHelper helper;

    private final CountersMap<ProcessingResult> counters = new CountersMap<ProcessingResult>();

    public RenameFiles() {
        this(null);
    }

    public RenameFiles(final IRenameHelper helper) {
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

    /**
     * @see org.ak2.fb2.library.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "cfn";
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        System.out.println("The 'Convert File Names' command is selected:\n\t" + args);

        // parsing parameters
        final String inputFolder = args.getValue(PARAM_INPUT);
        final String outputFolder = args.getValue(PARAM_OUTPUT);
        final OutputFormat outFormat = args.getValue(PARAM_OUTFORMAT, OutputFormat.class, OutputFormat.Zip);
        final OutputPath outPath = args.getValue(PARAM_OUTPATH, OutputPath.class, OutputPath.Standard);

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.");
        }

        if (LengthUtils.isEmpty(outputFolder)) {
            throw new BadCmdArguments("Output folder is missing.");
        }

        if (outFormat == null) {
            throw new BadCmdArguments("Output format is wrong.");
        }

        if (outPath == null) {
            throw new BadCmdArguments("Output path type is wrong.");
        }

        System.out.println("Processing input folder: " + inputFolder);
        System.out.println("Writing output into    : " + outputFolder);
        System.out.println("Output book format     : " + outFormat);
        System.out.println("Output book path type  : " + outPath);

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

        printResults();
    }

    public Collection<File> execute(final File inFile, final File outFile, final OutputFormat outFormat, final OutputPath outPath) {

        final Set<File> result = new LinkedHashSet<File>();

        FileScanner.enumerate(inFile, new IFileFilter() {
            @Override
            public boolean accept(final IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    try {
                        System.out.println("--------------------------------");
                        final File newFile = processFile(file, outFile, outFormat, outPath);
                        result.add(newFile);
                        counters.increment(ProcessingResult.CREATED);
                    } catch (final ProcessingException ex) {
                        final ProcessingResult pr = ex.getResult();
                        if (pr == ProcessingResult.FAILED) {
                            System.err.println("Error on processing " + file.getName() + ":");
                            ex.printStackTrace();
                        } else {
                            final File outFile = ex.getFile();
                            if (outFile != null) {
                                result.add(outFile);
                            }
                        }
                        counters.increment(pr);
                    } catch (final Throwable th) {
                        System.err.println("Error on processing " + file.getName() + ":");
                        th.printStackTrace();
                        counters.increment(ProcessingResult.FAILED);
                    }
                }
                return true;
            }
        }, new FileScanner.Options(true, true));

        return result;
    }

    public void printResults() {
        System.out.println("================================");
        System.out.println("Created new: " + counters.get(ProcessingResult.CREATED));
        System.out.println("Duplicated : " + counters.get(ProcessingResult.DUPLICATED));
        System.out.println("Failed     : " + counters.get(ProcessingResult.FAILED));
        System.out.println("================================");
    }

    public File processFile(final IFile file, final File outFile, final OutputFormat outFormat, final OutputPath outPath) throws ProcessingException {
        System.out.println("File        : " + file.getFullName());
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

            final Map<String, String> properties = helper.getBookProperties(book);

            String author = properties.get(IRenameHelper.AUTHOR_LAST_NAME) + " " + properties.get(IRenameHelper.AUTHOR_FIRST_NAME);
            String bookName = properties.get(IRenameHelper.BOOK_NAME);
            String seq = properties.get(IRenameHelper.BOOK_SEQUENCE);
            String seqNo = properties.get(IRenameHelper.BOOK_SEQUENCE_NO);

            helper.setBookProperties(book, properties);

            if (showInfo) {
                System.out.println("Author      : " + author);
                if (LengthUtils.isNotEmpty(seq)) {
                    System.out.println("Sequence    : " + seq);
                }
                if (LengthUtils.isNotEmpty(seqNo)) {
                    System.out.println("SequenceNo  : " + seqNo);
                }
                System.out.println("Book name   : " + bookName);
            }

            if (LengthUtils.isNotEmpty(seqNo)) {
                while (seqNo.length() < 3) {
                    seqNo = "0" + seqNo;
                }
                bookName = seqNo + ". " + bookName;
            }

            seq = fixName(seq);
            bookName = fixName(bookName);

            author = authors.getProperty(author, author);
            seq = series.getProperty(seq, seq);

            final File bookFolder = outPath.getFolder(outputFolder, author, seq);
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
