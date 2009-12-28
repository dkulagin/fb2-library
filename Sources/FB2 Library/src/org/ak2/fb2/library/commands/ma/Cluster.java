package org.ak2.fb2.library.commands.ma;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.commands.cfn.DefaultRenameHelper;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.common.OutputFormat;
import org.ak2.fb2.library.common.OutputPath;

public class Cluster extends DefaultRenameHelper {

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

    public void merge(final File outFolder, final OutputFormat outFormat, final OutputPath outPath) {

        System.out.println("Merge cluster: " + this);
        System.out.println("==================");

        RenameFiles cmd = new RenameFiles(this);
        for(Author a : authors) {
            cmd.execute(a.getFolder(), outFolder, outFormat, outPath);
        }
        cmd.printResults();
    }

    @Override
    public Map<String, String> getBookProperties(FictionBook book) {
        Map<String, String> bookProperties = super.getBookProperties(book);
        bookProperties.put(AUTHOR_FIRST_NAME, targetAuthor.getFirstName());
        bookProperties.put(AUTHOR_LAST_NAME, targetAuthor.getLastName());
        return bookProperties;
    }
}
