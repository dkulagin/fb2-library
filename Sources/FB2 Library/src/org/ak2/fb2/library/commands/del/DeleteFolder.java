package org.ak2.fb2.library.commands.del;

import java.io.File;
import java.io.FileFilter;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FolderScanner;

public class DeleteFolder implements ICommand {

    @Override
    public String getName() {
        return "del";
    }

    @Override
    public void execute(CommandArgs args) throws LibraryException {
        System.out.println("The 'Delete folder' command is selected:\n\t" + args);

        final String inputFolder = args.getValue(PARAM_INPUT);
        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input file is missing.");
        }

        System.out.println("==================");
        System.out.println("Folder to delete: " + inputFolder);

        final File inFolder = new File(inputFolder);

        if (!inFolder.isDirectory()) {
            throw new BadCmdArguments("Input folder is not exist.");
        }

        execute(inFolder);
    }

    public void execute(final File inFolder) {
        FolderScanner.enumerateWide(inFolder, new FolderFilter(), new FolderWorker(), Integer.MAX_VALUE);
    }

    private final class FolderFilter implements FileFilter {
        @Override
        public boolean accept(File folder) {
            return true;
        }
    }

    private final class FolderWorker implements FileFilter {
        @Override
        public boolean accept(File folder) {
            folder.listFiles(new FileWorker());
            boolean result = folder.delete();
            System.out.println((result ? "    Deleted" : "Not deleted") + " '"+folder.getAbsolutePath() + "'");
            return false;
        }
    }

    private final class FileWorker implements FileFilter {
        @Override
        public boolean accept(File file) {
            if (file.isFile()) {
                file.delete();
            }
            return false;
        }
    }
}
