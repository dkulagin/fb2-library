package org.ak2.fb2.library.commands.enc;

import java.io.File;
import java.io.IOException;

import org.ak2.fb2.library.book.XmlContent;
import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommandParameter;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.commands.parameters.EnumParameter;
import org.ak2.fb2.library.commands.parameters.FileSystemParameter;
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
import org.ak2.utils.jlog.JLogMessage;

public class FixEncoding extends AbstractCommand {

    private static final JLogMessage MSG_REPLACED = new JLogMessage(JLogLevel.DEBUG, "Replaced successfully");

    private static final ICommandParameter[] PARAMS = {
    /** -input  <library folder> - library folder */
    new FileSystemParameter(PARAM_INPUT, "library folder", true, false),
    /** -outformat <output book format> - output book format */
    new EnumParameter(PARAM_OUTFORMAT, "output book format", OutputFormat.values(), OutputFormat.Zip), };

    public FixEncoding() {
        super("enc");
    }

    /**
     * @see org.ak2.fb2.library.commands.ICommand#getParameters()
     */
    @Override
    public ICommandParameter[] getParameters() {
        return PARAMS;
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        MSG_ARGS.log(this.getClass().getSimpleName(), args);

        final String inputFolder = args.getValue("input");
        final OutputFormat outFormat = args.getValue(PARAM_OUTFORMAT, OutputFormat.class, OutputFormat.Zip);

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.", true);
        }

        if (outFormat == null) {
            throw new BadCmdArguments("Output format is wrong.", true);
        }

        logBoldLine();
        MSG_INFO_VALUE.log("Processing input folder ", inputFolder);
        MSG_INFO_VALUE.log("Output book format      ", outFormat);
        logBoldLine();

        final File inFolder = new File(inputFolder);
        final File tempFolder = createTempFolder(inFolder);

        final CountersMap<ProcessingResult> counters = new CountersMap<ProcessingResult>();

        FileScanner.enumerate(inFolder, new IFileFilter() {
            @Override
            public boolean accept(final IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    try {
                        logLine(JLogLevel.DEBUG);
                        final ProcessingResult result = fixEncoding(tempFolder, file, outFormat);
                        counters.increment(result);
                    } catch (ProcessingException ex) {
                        final ProcessingResult pr = ex.getResult();
                        if (pr == ProcessingResult.FAILED) {
                            MSG_ERROR.log(ex, file.getFullName());
                        }
                        counters.increment(pr);
                    } catch (final Exception ex) {
                        MSG_ERROR.log(ex, file.getFullName());
                        counters.increment(ProcessingResult.FAILED);
                    }
                }
                return true;
            }
        }, new FileScanner.Options(true, true));

        tempFolder.delete();

        logBoldLine();
        MSG_INFO_VALUE.log("Skipped   ", counters.get(ProcessingResult.SKIPPED));
        MSG_INFO_VALUE.log("Fixed     ", counters.get(ProcessingResult.CREATED));
        MSG_INFO_VALUE.log("Duplicated", counters.get(ProcessingResult.DUPLICATED));
        MSG_INFO_VALUE.log("Failed    ", counters.get(ProcessingResult.FAILED));
        logBoldLine();
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
        XmlContent content = new XmlContent(file);

        MSG_DEBUG_VALUE.log("File               ", file.getFullName());
        MSG_DEBUG_VALUE.log("Formal XML encoding", content.getXmlEncoding());
        MSG_DEBUG_VALUE.log("Real   XML encoding", content.getRealEncoding());

        if (!content.isWrongEncoding()) {
            return ProcessingResult.SKIPPED;
        }

        final RenameFiles renameCmd = new RenameFiles();
        final File realFile = file.getRealFile();

        final File newFile = renameCmd.createBookFile(content, tempFolder, outFormat, OutputPath.Simple, false);
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

        MSG_REPLACED.log();
        cf.delete();
        return ProcessingResult.CREATED;
    }
}
