package org.ak2.fb2.library.commands.ca;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.CompareUtils;
import org.ak2.utils.LengthUtils;

public class CompareAuthors implements ICommand {

    private List<Set<String>> clusters = new LinkedList<Set<String>>();

    @Override
    public void execute(CommandArgs args) throws LibraryException {
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

        File folder = new File(inputFolder);
        if (folder.isDirectory()) {
            processFolder(folder, depth.intValue(), distance.intValue());
        } else {
            throw new BadCmdArguments("Input folder is invalid.");
        }
        System.out.println("Printing clusters:");
        System.out.println("==================");
        for (Set<String> c : clusters) {
            for (Iterator<String> iterator = c.iterator(); iterator.hasNext();) {
                String name = iterator.next();
                System.out.print(name + (iterator.hasNext() ? ", " : ""));
            }
            System.out.println("");
        }

    }

    private void processFolder(File folder, int depth, int dist) {
        File[] subFolders = folder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (depth > 0) {
            for (File subFolder : subFolders) {
                processFolder(subFolder, depth - 1, dist);
            }
            return;
        }
        Arrays.sort(subFolders, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        System.out.println("Processing " + folder.getAbsolutePath());
        for (int i = 0; i < subFolders.length; i++) {
            Set<String> cluster = null;
            String nameI = subFolders[i].getName();
            for (Set<String> c : clusters) {
                if (c.contains(nameI)) {
                    cluster = c;
                    break;
                }
            }
            for (int j = 0; j < subFolders.length; j++) {
                String nameJ = subFolders[j].getName();
                if ((i != j) && CompareUtils.levensteinDistance(nameI, nameJ) <= dist) {
                    System.out.println("Adding '" + nameJ + "' to existing cluster");
                    if (cluster == null) {
                        cluster = new HashSet<String>();
                        cluster.add(nameI);
                        clusters.add(cluster);
                    }
                    cluster.add(nameJ);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "ca";
    }

}
