package org.ak2.fb2.library.commands.ca;

import java.io.File;

import org.ak2.utils.CompareUtils;

public class Author implements Comparable<Author> {

    private final File m_folder;

    private final String m_name;

    private String m_firstName;

    private String m_lastName;

    private boolean m_shortFirstName;

    public Author(final File folder) {
        m_folder = folder;
        m_name = folder.getName().trim();
        final int pos = m_name.lastIndexOf(' ');
        if (pos >= 0) {
            m_lastName = m_name.substring(0, pos).trim();
            m_firstName = m_name.substring(pos).trim();
            if (m_firstName.endsWith(".")) {
                m_firstName = m_firstName.substring(0, m_firstName.length() - 1);
            }
            m_shortFirstName = m_firstName.length() == 1;
        } else {
            m_lastName = m_name;
            m_firstName = null;
        }
    }

    public File getFolder() {
        return m_folder;
    }

    public String getName() {
        return m_name;
    }

    public String getFirstName() {
        return m_firstName;
    }

    public boolean isShortFirstName() {
        return m_shortFirstName;
    }

    public String getLastName() {
        return m_lastName;
    }

    @Override
    public String toString() {
        return m_name;
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
    public int compareTo(final Author obj) {
        if (this == obj) {
            return 0;
        }

        int result = this.m_name.compareToIgnoreCase(obj.m_name);
        if (result == 0) {
            result = this.m_folder.getAbsolutePath().compareTo(obj.m_folder.getAbsolutePath());
        }

        return result < 0 ? -1 : result > 0 ? 1 : 0;
    }

    public static boolean isSimilar(final Author a1, final Author a2, final int dist) {
        if (a1.m_firstName != null && a2.m_firstName != null) {
            final int lnDist = CompareUtils.levensteinDistance(a1.m_lastName, a2.m_lastName);
            if (lnDist > dist) {
                return false;
            }

            final boolean isShort1 = a1.isShortFirstName();
            final boolean isShort2 = a2.isShortFirstName();

            if (isShort1 && isShort2) {
                return a1.m_firstName.equalsIgnoreCase(a2.m_firstName);
            } else if (isShort1) {
                return lnDist == 0 && a2.m_firstName.startsWith(a1.m_firstName);
            } else if (isShort2) {
                return lnDist == 0 && a1.m_firstName.startsWith(a2.m_firstName);
            }

            final int fnDist = CompareUtils.levensteinDistance(a1.m_firstName, a2.m_firstName);

            return lnDist + fnDist <= dist;

        } else {
            final boolean isShort1 = a1.m_name.length() == 1;
            final boolean isShort2 = a2.m_name.length() == 1;

            if (isShort1 && isShort2) {
                return a1.m_name.equalsIgnoreCase(a2.m_name);
            }

            return CompareUtils.levensteinDistance(a1.m_name, a2.m_name) <= dist;
        }
    }

}
