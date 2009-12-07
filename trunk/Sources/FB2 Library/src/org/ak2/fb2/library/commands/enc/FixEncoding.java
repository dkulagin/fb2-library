package org.ak2.fb2.library.commands.enc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.common.Encoding;
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

public class FixEncoding implements ICommand {

    @Override
    public String getName() {
        return "enc";
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        System.out.println("The 'Fix XML encoding' command is selected:\n\t" + args);

        final String inputFolder = args.getValue("input");
        final OutputFormat outFormat = args.getValue(PARAM_OUTFORMAT, OutputFormat.class, OutputFormat.Zip);

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.");
        }

        if (outFormat == null) {
            throw new BadCmdArguments("Output format is wrong.");
        }

        System.out.println("Processing input folder : " + inputFolder);
        System.out.println("Output book format      : " + outFormat);

        final File inFolder = new File(inputFolder);
        final File tempFolder = createTempFolder(inFolder);

        final CountersMap<ProcessingResult> counters = new CountersMap<ProcessingResult>();

        FileScanner.enumerate(inFolder, new IFileFilter() {
            @Override
            public boolean accept(final IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    try {
                        System.out.println("--------------------------------");
                        final ProcessingResult result = fixEncoding(tempFolder, file, outFormat);
                        counters.increment(result);
                    } catch (final Exception ex) {
                        System.err.println("Error on processing " + file.getName() + ":\n\t" + ex.getMessage());
                        counters.increment(ProcessingResult.FAILED);
                    }
                }
                return true;
            }
        }, new FileScanner.Options(true, true));

        tempFolder.delete();

        System.out.println("================================");
        System.out.println("Skipped    : " + counters.get(ProcessingResult.SKIPPED));
        System.out.println("Fixed      : " + counters.get(ProcessingResult.CREATED));
        System.out.println("Duplicated : " + counters.get(ProcessingResult.DUPLICATED));
        System.out.println("Failed     : " + counters.get(ProcessingResult.FAILED));
    }

    private File createTempFolder(final File inFolder) throws LibraryException {
        try {
            final File tempFolder = File.createTempFile("temp.", "", inFolder);
            tempFolder.delete();
            tempFolder.mkdirs();
            if (!tempFolder.exists()) {
                throw new IOException("Temp folder '" + tempFolder.getAbsolutePath() + "' not created");
            }
            return tempFolder;
        } catch (final IOException ex1) {
            throw new LibraryException("Temp folder cannot be created: " + ex1.getMessage());
        }
    }

    private ProcessingResult fixEncoding(final File tempFolder, final IFile file, final OutputFormat outFormat) throws Exception {
        final Encoding encoding = getEncoding(file);
        if (encoding == null) {
            return ProcessingResult.SKIPPED;
        }
        final StringBuilder text = loadText(file, encoding.name());
        final String oldEncoding = FictionBook.getXmlEncoding(text, null);
        final String newEncoding = encoding.getXmlName();

        System.out.println("File               : " + file.getFullName());
        System.out.println("Formal XML encoding: " + oldEncoding);
        System.out.println("Real   XML encoding: " + newEncoding);

        if (LengthUtils.equalsIgnoreCase(oldEncoding, newEncoding)) {
            return ProcessingResult.SKIPPED;
        }

        FictionBook.fixXmlEncoding(text, newEncoding);
        final byte[] content = text.toString().getBytes(encoding.name());
        final ByteArrayInputStream inStream = new ByteArrayInputStream(content);

        final RenameFiles renameCmd = new RenameFiles();
        final File realFile = file.getRealFile();

        final File newFile = renameCmd.createBookFile(inStream, tempFolder, outFormat, OutputPath.Simple, false, false);
        if (!newFile.exists()) {
            throw new LibraryException("Replacement not created for " + file.getName());
        }

        final File cf = new File(tempFolder, "Old." + realFile.getName());
        if (!realFile.renameTo(cf)) {
            throw new LibraryException("Original file cannot be removed: " + realFile.getName());
        }

        final File replace = new File(realFile.getParentFile(), newFile.getName());
        if (!newFile.renameTo(replace)) {
            throw new LibraryException("Replacement file could not be copied: " + newFile.getAbsolutePath());
        }

        System.out.println("Replaced successfully");
        cf.delete();
        return ProcessingResult.CREATED;
    }

    public Encoding getEncoding(final IFile file) throws IOException {
        final InputStream inStream = file.open();
        try {
            return getEncoding(inStream);
        } finally {
            try {
                inStream.close();
            } catch (final Exception ex) {
            }
        }
    }

    public Encoding getEncoding(final InputStream inStream) throws IOException {
        final int utf8Length = Encoding.UTF8.getPattern().length;
        final int winLength = Encoding.CP1251.getPattern().length;
        final int[] buf = new int[Math.max(utf8Length, winLength)];

        int head = 0;
        int tail = 0;

        for (int val = inStream.read(); val != -1; val = inStream.read()) {
            buf[tail] = val;
            tail = (tail + 1) % buf.length;
            final int bufLength = (buf.length + tail - head) % buf.length;

            for (final Encoding enc : Encoding.values()) {
                final int[] pattern = enc.getPattern();
                final int encLength = pattern.length;
                if (bufLength >= encLength) {
                    boolean exact = true;
                    for (int i = 0; i < encLength && exact; i++) {
                        exact = pattern[i] == buf[head + i];
                    }
                    if (exact) {
                        return enc;
                    }
                }
            }
            if (bufLength == buf.length) {
                head = (head + 1) % buf.length;
            }
        }
        return null;
    }

    public StringBuilder loadText(final IFile file, final String encoding) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(file.open(), encoding));
        final StringBuilder text = new StringBuilder();
        try {
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                text.append(s).append("\n");
            }
            return text;
        } finally {
            try {
                reader.close();
            } catch (final Exception ex) {
            }
        }
    }

}
