package org.ak2.fb2.library.commands.ca;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.CompareUtils;
import org.ak2.utils.LengthUtils;

public class CompareAuthors implements ICommand {

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
            for (int j = i + 1; j < subFolders.length; j++) {
                if (CompareUtils.levensteinDistance(subFolders[i].getName(), subFolders[j].getName()) <= dist) {
                    System.out.println("'" + subFolders[i].getName() + "' is similiar to '" + subFolders[j].getName() + "'");
                }
            }
        }
    }

    @Override
    public String getName() {
        return "ca";
    }

}
