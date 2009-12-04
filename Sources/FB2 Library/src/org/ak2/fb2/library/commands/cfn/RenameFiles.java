package org.ak2.fb2.library.commands.cfn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
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

    public RenameFiles() {
        try {
            authors.load(RenameFiles.class.getResourceAsStream("Authors.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            series.load(RenameFiles.class.getResourceAsStream("Series.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "cfn";
    }

    @Override
    public void execute(CommandArgs args) throws LibraryException {
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
        final File outFile = new File(outputFolder);

        if (inFile.equals(outFile)) {
            throw new BadCmdArguments("Input and output folders cannot be the same.");
        }
        outFile.mkdirs();
        if (!outFile.exists()) {
            throw new BadCmdArguments("Output folder is not exist");
        }

        final CountersMap<ProcessingResult> counters = new CountersMap<ProcessingResult>();

        FileScanner.enumerate(inFile, new IFileFilter() {
            @Override
            public boolean accept(IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    try {
                        System.out.println("--------------------------------");
                        ProcessingResult result = processFile(file, outFile, outFormat, outPath);
                        counters.increment(result);
                    } catch (Exception ex) {
                        System.err.println("Error on processing " + file.getName() + ":");
                        ex.printStackTrace();
                        counters.increment(ProcessingResult.FAILED);
                    }
                }
                return true;
            }
        }, new FileScanner.Options(true, true));

        System.out.println("================================");
        System.out.println("Created new: " + counters.get(ProcessingResult.CREATED));
        System.out.println("Duplicated : " + counters.get(ProcessingResult.DUPLICATED));
        System.out.println("Failed     : " + counters.get(ProcessingResult.FAILED));
    }

    public ProcessingResult processFile(IFile file, final File outFile, final OutputFormat outFormat, final OutputPath outPath) throws Exception,
            IOException {
        File newFile = createBookFile(file.open(), outFile, outFormat, outPath, true);
        ProcessingResult result = newFile != null ? ProcessingResult.CREATED : ProcessingResult.DUPLICATED;
        return result;
    }

    public File createBookFile(InputStream inStream, File outputFolder, OutputFormat outFormat, final OutputPath outPath,
            boolean showInfo) throws Exception {

        FictionBook book = new FictionBook(inStream);

        String author = book.getAuthor();
        String seq = book.getSequence();
        String seqNo = book.getSequenceNo();
        String bookName = book.getBookName();
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

// 		I commented this out because of some sequences without numbers. For example collections of short stories. AK.
//        if (LengthUtils.isNotEmpty(seq) && LengthUtils.isEmpty(seqNo)) {
//            seqNo = "001";
//        }
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

        File bookFolder = outPath.getFolder(outputFolder, author, seq);
        String bookFileName = bookName + ".fb2";
        return outFormat.createFile(bookFolder, bookFileName, book);
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
