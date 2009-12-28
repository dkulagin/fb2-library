package org.ak2.fb2.library.commands.ma;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.commands.cfn.DefaultRenameHelper;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.commands.del.DeleteFolder;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class Cluster extends DefaultRenameHelper {

    private static final JLogMessage MSG_MERGE = new JLogMessage(JLogLevel.DEBUG, "Merge cluster: {0}");

    private final Author targetAuthor;

    private final List<Author> authors = new LinkedList<Author>();

    public Cluster(final Author targetAuthor) {
        this.targetAuthor = targetAuthor;
    }

    public void addFolder(final Author author) {
        if (author.getFolder() != null) {
            authors.add(author);
        }
    }

    @Override
    public String toString() {
        return "Cluster [" + "targetAuthor=" + targetAuthor + ", " + "authors=" + authors + "]";
    }

    public void merge(final File outFolder, final OutputFormat outFormat, final OutputPath outPath, final boolean delete) {

        MSG_MERGE.log(this);

        final RenameFiles mCmd = new RenameFiles(this);
        final DeleteFolder dCmd = new DeleteFolder();

        Set<File> allFiles = new LinkedHashSet<File>();

        for (final Author author : authors) {
            final File folder = author.getFolder();
            Collection<File> files = mCmd.execute(folder, outFolder, outFormat, outPath);
            allFiles.addAll(files);
        }

        mCmd.printResults();

        if (delete) {
            for (final Author author : authors) {
                File folder = author.getFolder();
                String path = folder.getAbsolutePath() + "/";
                boolean deleteFolder = true;
                for (File file : allFiles) {
                    if (file.getAbsolutePath().startsWith(path)) {
                        deleteFolder = false;
                        break;
                    }
                }
                if (deleteFolder) {
                    dCmd.execute(folder);
                }
            }
        }
    }

    @Override
    public Map<String, String> getBookProperties(final FictionBook book) {
        final Map<String, String> bookProperties = super.getBookProperties(book);
        bookProperties.put(AUTHOR_FIRST_NAME, targetAuthor.getFirstName());
        bookProperties.put(AUTHOR_LAST_NAME, targetAuthor.getLastName());
        return bookProperties;
    }
}
