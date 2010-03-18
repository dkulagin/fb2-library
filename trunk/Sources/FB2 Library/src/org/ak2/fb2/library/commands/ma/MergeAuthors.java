package org.ak2.fb2.library.commands.ma;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommandParameter;
import org.ak2.fb2.library.commands.parameters.BoolParameter;
import org.ak2.fb2.library.commands.parameters.EnumParameter;
import org.ak2.fb2.library.commands.parameters.FileSystemParameter;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.StreamUtils;
import org.ak2.utils.csv.CsvRecord;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class MergeAuthors extends AbstractCommand {

    private static final JLogMessage MSG_PROCESS = new JLogMessage("Process clusters:");

    private static final ICommandParameter[] PARAMS = {
    /** -input <input file> - file with list of similar author name */
    new FileSystemParameter(PARAM_INPUT, "file with list of similar author name", false, true),
    /** -output <target folder> - folder to store books of merged authors */
    new FileSystemParameter(PARAM_OUTPUT, "folder to store books of merged authors", true, false),
    /** -outpath <output path type> - output path type */
    new EnumParameter(PARAM_OUTPATH, "output path type", OutputPath.values(), OutputPath.Standard),
    /** -outformat <output book format> - output book format */
    new EnumParameter(PARAM_OUTFORMAT, "output book format", OutputFormat.values(), OutputFormat.Zip),
    /** -delete <true|false> - delete or not old authors folders */
    new BoolParameter(PARAM_DELETE, "delete or not old authors folders", false), };

    public MergeAuthors() {
        super("ma");
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

        final String inputFile = args.getValue(PARAM_INPUT);
        final String outputFolder = args.getValue(PARAM_OUTPUT);
        final OutputFormat outFormat = args.getValue(PARAM_OUTFORMAT, OutputFormat.class, OutputFormat.Zip);
        final OutputPath outPath = args.getValue(PARAM_OUTPATH, OutputPath.class, OutputPath.Standard);
        final boolean delete = args.getValue(PARAM_DELETE, false);

        if (LengthUtils.isEmpty(inputFile)) {
            throw new BadCmdArguments("Input file is missing.", true);
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
        MSG_INFO_VALUE.log("Processing input file", inputFile);
        MSG_INFO_VALUE.log("Writing output into  ", outputFolder);
        MSG_INFO_VALUE.log("Output book format   ", outFormat);
        MSG_INFO_VALUE.log("Output book path type", outPath);
        logBoldLine();

        final File inFile = new File(inputFile);
        final File outFolder = new File(outputFolder);

        if (!inFile.isFile()) {
            throw new BadCmdArguments("Input file is not exist.");
        }

        outFolder.mkdirs();
        if (!outFolder.exists()) {
            throw new BadCmdArguments("Output folder is not exist");
        }

        final List<Cluster> clusters = loadClusters(inFile);

        MSG_PROCESS.log();
        logBoldLine(JLogLevel.INFO);

        for (final Cluster cluster : clusters) {
            cluster.merge(outFolder, outFormat, outPath, delete);
        }
    }

    private List<Cluster> loadClusters(final File inFile) {
        try {
            return StreamUtils.loadText(new FileReader(inFile), new Loader());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    static class Loader extends StreamUtils.AsbstractTextLoader<List<Cluster>> {

        private Cluster c = null;

        public Loader() {
            super(new LinkedList<Cluster>());
        }

        @Override
        public boolean onLine(final String line) {
            if (LengthUtils.isEmpty(line)) {
                c = null;
            } else {
                final Author author = getAuthor(line);
                if (author != null) {
                    if (c == null) {
                        c = new Cluster(author);
                        m_result.add(c);
                    }
                    c.addFolder(author);
                }
            }
            return true;
        }

        Author getAuthor(final String s) {
            final CsvRecord rec = new CsvRecord(s);
            final int size = rec.size();
            if (size > 0) {
                final String name = rec.getField(0);
                if (size == 2) {
                    final String path = rec.getField(1);
                    return new Author(name, new File(path));
                }
                return new Author(name, null);
            }
            return null;
        }

    }

}
