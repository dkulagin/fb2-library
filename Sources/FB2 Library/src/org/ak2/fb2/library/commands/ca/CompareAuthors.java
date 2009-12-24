package org.ak2.fb2.library.commands.ca;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.TreeSet;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FolderScanner;

public class CompareAuthors implements ICommand {

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        System.out.println("The 'Compare authors' command is selected:\n\t" + args);

        // parsing parameters
        final String inputFolder = args.getValue(PARAM_INPUT);
        final String outputFile = args.getValue(PARAM_OUTPUT);
        final int depth = args.getValue(PARAM_DEPTH, 0);
        final int distance = args.getValue(PARAM_DISTANCE, 1);

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.");
        }

        if (LengthUtils.isEmpty(outputFile)) {
            throw new BadCmdArguments("Output file is missing.");
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
        for (final Set<Author> c : clusters.getClusters()) {
            System.out.println(Clusters.toString(c));
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

    @Override
    public String getName() {
        return "ca";
    }

}
