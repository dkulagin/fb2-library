package org.ak2.fb2.library.commands.del;

import java.io.File;
import java.io.FileFilter;

import org.ak2.fb2.library.commands.AbstractCommand;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FolderScanner;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class DeleteFolder extends AbstractCommand {

    private static final JLogMessage MSG_DELETE = new JLogMessage(JLogLevel.DEBUG, "{0}: '''{0}'''");

    public DeleteFolder() {
        super("del");
    }

    @Override
    public void execute(CommandArgs args) throws LibraryException {
        MSG_ARGS.log(this.getClass().getSimpleName(), args);

        final String inputFolder = args.getValue(PARAM_INPUT);
        if (LengthUtils.isEmpty(inputFolder)) {
            throw new BadCmdArguments("Input file is missing.", true);
        }

        logBoldLine(MSG_INFO_VALUE.getLevel());
        MSG_INFO_VALUE.log("Folder to delete", inputFolder);
        logBoldLine(MSG_INFO_VALUE.getLevel());

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
            MSG_DELETE.log(result ? "    Deleted" : "Not deleted", folder.getAbsolutePath());
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
