package org.ak2.fb2.library.commands.ca;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.CompareUtils;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FolderScanner;

public class CompareAuthors implements ICommand {

    private final List<Set<String>> clusters = new LinkedList<Set<String>>();

    @Override
    public void execute(final CommandArgs args) throws LibraryException {
        System.out.println("The 'Compare authors' command is selected:\n\t" + args);

        // parsing parameters
        final String inputFolder = args.getValue(PARAM_INPUT);
        final String outputFile = args.getValue(PARAM_OUTPUT);
        final Integer depth = Integer.valueOf(args.getValue(PARAM_DEPTH, "0"));
        final Integer distance = Integer.valueOf(args.getValue(PARAM_DISTANCE, "1"));

        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input folder is missing.");
        }

        if (LengthUtils.isEmpty(outputFile)) {
            throw new BadCmdArguments("Output file is missing.");
        }

        final File folder = new File(inputFolder);
        if (folder.isDirectory()) {
            processFolder(folder, depth.intValue(), distance.intValue());
        } else {
            throw new BadCmdArguments("Input folder is invalid.");
        }
        System.out.println("Printing clusters:");
        System.out.println("==================");
        for (final Set<String> c : clusters) {
            System.out.println(c);
        }

    }

    private void processFolder(final File folder, final int depth, final int dist) {
        final File[] subFolders = getSubFolders(folder, depth);
        for (int i = 0; i < subFolders.length; i++) {
            System.out.println("Processing '" + subFolders[i].getAbsolutePath() + "' ...");
            final String nameI = subFolders[i].getName();
            Set<String> cluster = getCluster(nameI);
            for (int j = 0; j < subFolders.length; j++) {
                final String nameJ = subFolders[j].getName();
                if (i != j && CompareUtils.levensteinDistance(nameI, nameJ) <= dist) {
                    System.out.println("Adding '" + nameJ + "' to existing cluster");
                    if (cluster == null) {
                        cluster = new TreeSet<String>();
                        cluster.add(nameI);
                        clusters.add(cluster);
                    }
                    cluster.add(nameJ);
                }
            }
        }
    }

    private File[] getSubFolders(final File folder, final int depth) {
        final Set<File> folders = new TreeSet<File>(new Comparator<File>() {
            @Override
            public int compare(final File o1, final File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        FolderScanner.enumerateDepth(folder, new FileFilter() {
            @Override
            public boolean accept(final File f) {
                folders.add(f);
                return true;
            }
        }, depth);

        final File[] subFolders = folders.toArray(new File[folders.size()]);
        return subFolders;
    }

    private Set<String> getCluster(final String nameI) {
        for (final Set<String> c : clusters) {
            if (c.contains(nameI)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "ca";
    }

}
