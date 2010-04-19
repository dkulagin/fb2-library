package org.ak2.fb2.library.commands.ca;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.ak2.fb2.library.book.BookAuthor;
import org.ak2.utils.CompareUtils;
import org.ak2.utils.FileUtils;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.FileScanner;
import org.ak2.utils.files.IFile;
import org.ak2.utils.files.IFileFilter;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

public class Author extends BookAuthor {

    private static final JLogMessage MSG_SCAN = new JLogMessage(JLogLevel.INFO, "Scan folder {0}");

    private final File m_folder;

    private final boolean m_shortFirstName;

    private Set<String> m_files;

    public Author(final File folder) {
        super(folder.getName());
        m_folder = folder;
        m_shortFirstName = getFirstName().length() == 1;
    }

    public File getFolder() {
        return m_folder;
    }

    public boolean isShortFirstName() {
        return m_shortFirstName;
    }

    public Set<String> getFiles() {
        if (m_files == null) {
            m_files = new TreeSet<String>();
            MSG_SCAN.log(m_folder);
            FileScanner.enumerate(m_folder, new IFileFilter() {
                @Override
                public boolean accept(final IFile file) {
                    if (file.getName().endsWith(".fb2")) {
                        final File realFile = file.getRealFile();
                        final String fileName = FileUtils.getRelativeFileName(realFile, m_folder);
                        m_files.add(fileName);
                    }
                    return true;
                }
            }, new FileScanner.Options(true, true));
        }
        return m_files;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return this.m_folder.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Author) {
            return 0 == compareTo((Author) obj);
        }
        return false;
    }

    @Override
    public int compareTo(final BookAuthor that) {
        int result = super.compareTo(that);
        if (result == 0 && that instanceof Author) {
            result = this.m_folder.getAbsolutePath().compareTo(((Author) that).m_folder.getAbsolutePath());
        }

        return result < 0 ? -1 : result > 0 ? 1 : 0;
    }

    public static boolean isSimilar(final Author a1, final Author a2, final int dist) {
        final String fn1 = a1.getFirstName();
        final String fn2 = a2.getFirstName();
        final String ln1 = a1.getLastName();
        final String ln2 = a2.getLastName();

        if (LengthUtils.equals(fn1, ln2) && LengthUtils.equals(ln1, fn2)) {
            return true;
        }
        if (fn1 != null && fn2 != null) {
            final int lnDist = CompareUtils.levensteinDistance(ln1, ln2);
            if (lnDist > dist) {
                return false;
            }

            final boolean isShort1 = a1.isShortFirstName();
            final boolean isShort2 = a2.isShortFirstName();

            if (isShort1 && isShort2) {
                return fn1.equalsIgnoreCase(fn2);
            } else if (isShort1) {
                return lnDist == 0 && fn2.startsWith(fn1);
            } else if (isShort2) {
                return lnDist == 0 && fn1.startsWith(fn2);
            }

            final int fnDist = CompareUtils.levensteinDistance(fn1, fn2);

            return lnDist + fnDist <= dist;

        } else {
            final String n1 = a1.getName();
            final String n2 = a2.getName();

            final boolean isShort1 = n1.length() == 1;
            final boolean isShort2 = n2.length() == 1;

            if (isShort1 && isShort2) {
                return n1.equalsIgnoreCase(n2);
            }

            return CompareUtils.levensteinDistance(n1, n2) <= dist;
        }
    }

}
