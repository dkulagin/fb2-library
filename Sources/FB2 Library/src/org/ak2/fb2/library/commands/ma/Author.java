package org.ak2.fb2.library.commands.ma;

import java.io.File;

import org.ak2.utils.LengthUtils;

public class Author {

    private final File m_folder;

    private String m_name;

    private String m_firstName;

    private String m_lastName;

    public Author(final String name, final File folder) {
        m_folder = folder;
        m_name = name;
        if (LengthUtils.isNotEmpty(name)) {
            int pos = m_name.indexOf("/");
            if (pos >= 0) {
                m_lastName = m_name.substring(0, pos).trim();
                m_firstName = m_name.substring(pos + 1).trim();
                if (m_firstName.endsWith(".")) {
                    m_firstName = m_firstName.substring(0, m_firstName.length() - 1);
                }
                m_name = (m_lastName + " " + m_firstName).trim();
            } else {
                pos = m_name.lastIndexOf(' ');
                if (pos >= 0) {
                    m_lastName = m_name.substring(0, pos).trim();
                    m_firstName = m_name.substring(pos).trim();
                    if (m_firstName.endsWith(".")) {
                        m_firstName = m_firstName.substring(0, m_firstName.length() - 1);
                    }
                } else {
                    m_lastName = m_name;
                    m_firstName = null;
                }
            }
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

    public String getLastName() {
        return m_lastName;
    }

    @Override
    public String toString() {
        return "Author [m_name=" + m_name + ", m_lastName=" + m_lastName + ", m_firstName=" + m_firstName + ", m_folder=" + m_folder + "]";
    }
}
