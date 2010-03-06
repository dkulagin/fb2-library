package org.ak2.fb2.library.commands.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.parsers.ParserConfigurationException;

import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommandParameter;
import org.ak2.fb2.library.commands.parameters.FileSystemParameter;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.FileUtils;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FileScanner;
import org.ak2.utils.files.IFile;
import org.ak2.utils.files.IFileFilter;
import org.ak2.utils.files.IFolder;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.xml.sax.SAXException;

/**
 * @author Alexander Kasatkin
 */
public class ExportXml extends AbstractCommand {

    private static final ICommandParameter[] PARAMS = {
    /** -input  <path list> - input file or folders separated by standard system path separator */
    new FileSystemParameter(PARAM_INPUT, "input file or folder", true, true),
    /** -output <target file> - xml file to store book catalog */
    new FileSystemParameter(PARAM_OUTPUT, "xml file to store book catalog", false, true), };

    private final BookTitleInfoHandler m_handler = new BookTitleInfoHandler();

    public ExportXml() {
        super("xml");
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

        // parsing parameters
        final String inputFolders = args.getValue(PARAM_INPUT);
        final String outputFile = args.getValue(PARAM_OUTPUT);

        if (LengthUtils.isEmpty(inputFolders)) {
            throw new BadCmdArguments("Input folders are missing.", true);
        }

        if (LengthUtils.isEmpty(outputFile)) {
            throw new BadCmdArguments("Output file is missing.", true);
        }

        final List<File> locations = CommandArgs.getLocations(inputFolders);
        if (LengthUtils.isEmpty(locations)) {
            throw new BadCmdArguments("No input locations exist");
        }

        logBoldLine();
        MSG_INFO_VALUE.log("Processing locations", locations);
        MSG_INFO_VALUE.log("Writing output into ", outputFile);
        logBoldLine();

        final File outFile = new File(outputFile);

        final File outFolder = outFile.getAbsoluteFile().getParentFile();
        outFolder.mkdirs();
        if (!outFolder.exists()) {
            throw new BadCmdArguments("Output folder is not exist");
        }

        execute(locations, outFile);
    }

    public void execute(final List<File> inFiles, final File outFile) {

        try {
            final FileWriter out = new FileWriter(outFile);
            try {
                out.append("<books>\n");
                out.flush();

                for (final File inFile : inFiles) {
                    processLocation(out, inFile);
                }

                out.append("</books>\n");
                out.flush();
            } catch (final IOException ex) {
                MSG_ERROR.log(ex, outFile.getAbsolutePath());
            } finally {
                out.close();
            }
        } catch (final IOException ex) {
            MSG_ERROR.log(ex, outFile.getAbsolutePath());
        }
    }

    private void processLocation(final FileWriter out, final File inFile) throws IOException {
        out.append("<location base=\"").append(inFile.getAbsolutePath()).append("\">\n");
        out.flush();

        MSG_INFO_VALUE.log("Location", inFile.getAbsolutePath());

        final AtomicLong counter = new AtomicLong();

        FileScanner.enumerate(inFile, new IFileFilter() {
            @Override
            public boolean accept(final IFile file) {
                if (file.getName().endsWith(".fb2")) {
                    try {
                        processFile(out, inFile, file);
                    } catch (final Throwable th) {
                        MSG_ERROR.log(th, file.getFullName());
                    } finally {
                        final long val = counter.incrementAndGet();
                        if (val % 1000 == 0) {
                            MSG_INFO_VALUE.log("Processed", val);
                            try {
                                out.flush();
                            } catch (final IOException ex) {
                                MSG_ERROR.log(ex, file.getFullName());
                            }
                        }
                    }
                }
                return true;
            }
        }, new FileScanner.Options(true, true));

        MSG_INFO_VALUE.log("Finished ", counter.get());
        logBoldLine();

        out.append("</location>\n");
        out.flush();
    }

    private void processFile(final FileWriter out, final File inFile, final IFile file) throws IOException, ParserConfigurationException, SAXException {
        final IFolder parent = file.getParent();
        MSG_DEBUG_VALUE.log("File", FileUtils.getRelativeFileName(file.getFullName(), inFile));

        final String title = m_handler.parse(file);
        if (LengthUtils.isNotEmpty(title)) {
            out.append("<book ");
            out.append("container=\"").append(FileUtils.getRelativeFileName(parent.getFullName(), inFile)).append("\"");
            out.append(" ");
            out.append("file=\"").append(file.getName()).append("\"");
            out.append(">");
            out.append(title);
            out.append("</book>\n");
            // out.flush();
        } else {
            new JLogMessage(JLogLevel.WARNING, "File {0}: no title-info found").log(file.getFullName());
        }
    }
}
