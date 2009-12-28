package org.ak2.fb2.library.commands.ma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;

public class MergeAuthors extends AbstractCommand {

    public MergeAuthors() {
        super("ma");
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        System.out.println("The 'Merge authors' command is selected:\n\t" + args);

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

        System.out.println("==================");
        System.out.println("Processing input file  : " + inputFile);
        System.out.println("Writing output into    : " + outputFolder);
        System.out.println("Output book format     : " + outFormat);
        System.out.println("Output book path type  : " + outPath);
        System.out.println("==================");

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

        System.out.println("Process clusters:");
        System.out.println("==================");

        for (final Cluster cluster : clusters) {
            cluster.merge(outFolder, outFormat, outPath, delete);
        }
    }

    private List<Cluster> loadClusters(final File inFile) {
        final List<Cluster> clusters = new LinkedList<Cluster>();
        try {
            final BufferedReader in = new BufferedReader(new FileReader(inFile));

            Cluster c = null;
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                if (LengthUtils.isEmpty(s)) {
                    c = null;
                } else {
                    final Author author = getAuthor(s);
                    if (c == null) {
                        c = new Cluster(author);
                        clusters.add(c);
                    }
                    c.addFolder(author);
                }
            }

            try {
                in.close();
            } catch (final IOException ex) {
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return clusters;
    }

    private Author getAuthor(final String s) {
        final String[] parts = s.split(":");
        final String name = normalize(parts[0]);
        if (parts.length > 1) {
            return new Author(name, new File(normalize(parts[1])));
        }
        return new Author(name, null);
    }

    private String normalize(final String s) {
        final String val = s.trim();
        int firstIndex = 0;
        int lastIndex = val.length();
        if (val.startsWith("'")) {
            firstIndex++;
        }
        if (val.endsWith("'")) {
            lastIndex--;
        }
        return val.substring(firstIndex, lastIndex).trim();
    }
}
