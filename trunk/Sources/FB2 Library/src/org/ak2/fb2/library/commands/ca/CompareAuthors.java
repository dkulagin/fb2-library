package org.ak2.fb2.library.commands.ca;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FolderScanner;
import org.ak2.utils.jlog.JLog;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class CompareAuthors extends AbstractCommand {

    private static final JLogMessage MSG_SCAN = new JLogMessage(JLogLevel.INFO, "Scan folders:");

    private static final JLogMessage MSG_CHECK = new JLogMessage(JLogLevel.INFO, "Check authors:");

    private static final JLogMessage MSG_CLUSTERS = new JLogMessage(JLogLevel.INFO, "Printing clusters:");

    public CompareAuthors() {
        super("ca");
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        MSG_ARGS.log(this.getClass().getSimpleName(), args);

        // parsing parameters
        final String inputFolder = args.getValue(PARAM_INPUT);
        final String outputFile = args.getValue(PARAM_OUTPUT);
        final int depth = args.getValue(PARAM_DEPTH, 0);
        final int distance = args.getValue(PARAM_DISTANCE, 1);

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.", true);
        }

        if (LengthUtils.isEmpty(outputFile)) {
            throw new BadCmdArguments("Output file is missing.", true);
        }

        final File folder = new File(inputFolder);
        if (!folder.isDirectory()) {
            throw new BadCmdArguments("Input folder is invalid.");
        }

        logBoldLine(MSG_INFO_VALUE.getLevel());
        MSG_INFO_VALUE.log("Processing folder ", inputFolder);
        MSG_INFO_VALUE.log("Output file       ", outputFile);
        MSG_INFO_VALUE.log("Scanning depth    ", depth);
        MSG_INFO_VALUE.log("Comparing distance", distance);
        logBoldLine(MSG_INFO_VALUE.getLevel());
        
        MSG_SCAN.log();
        logBoldLine(JLogLevel.DEBUG);
        final Author[] authors = getAuthors(folder, depth);

        MSG_CHECK.log();
        logBoldLine(JLogLevel.DEBUG);
        Clusters clusters = new Clusters(authors, distance);

        MSG_CLUSTERS.log();
        logBoldLine(JLogLevel.INFO);

        try {
            PrintWriter out = new PrintWriter(new FileWriter(outputFile));
            for (final Set<Author> cluster : clusters.getClusters()) {
                String str = Clusters.toString(cluster);
                JLog.log(JLogLevel.INFO.getLevel(), str);
                out.println(str);
            }
            try {
                out.close();
            } catch (Exception ex) {
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Author[] getAuthors(final File folder, final int depth) {
        final Set<Author> authors = new TreeSet<Author>();
        FolderScanner.enumerateDepth(folder, new FileFilter() {
            @Override
            public boolean accept(final File f) {
                authors.add(new Author(f));
                return true;
            }
        }, depth);

        return authors.toArray(new Author[authors.size()]);
    }
}
