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

public class CompareAuthors extends AbstractCommand {

    public CompareAuthors() {
        super("ca");
    }

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        System.out.println("The 'Compare authors' command is selected:\n\t" + args);

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

        System.out.println("Scan folders:");
        System.out.println("==================");
        final Author[] authors = getAuthors(folder, depth);

        System.out.println("Check authors:");
        System.out.println("==================");
        Clusters clusters = new Clusters(authors, distance);

        System.out.println("Printing clusters:");
        System.out.println("==================");

        try {
            PrintWriter out = new PrintWriter(new FileWriter(outputFile));
            for (final Set<Author> cluster : clusters.getClusters()) {
                String str = Clusters.toString(cluster);
                System.out.println(str);
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
