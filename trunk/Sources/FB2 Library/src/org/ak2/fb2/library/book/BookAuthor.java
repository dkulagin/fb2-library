package org.ak2.fb2.library.book;

import org.ak2.utils.LengthUtils;

public class BookAuthor {

    private String m_name;

    private String m_firstName;

    private String m_lastName;

    public BookAuthor(final String name) {
        this(name, true);
    }

    public BookAuthor(final String name, final boolean lastFirst) {
        m_name = LengthUtils.safeString(name).trim();
        if (LengthUtils.isNotEmpty(name)) {
            int pos = m_name.indexOf("/");
            if (pos >= 0) {
                if (lastFirst) {
                    m_lastName = m_name.substring(0, pos).trim();
                    m_firstName = m_name.substring(pos + 1).trim();
                } else {
                    m_firstName = m_name.substring(0, pos).trim();
                    m_lastName = m_name.substring(pos + 1).trim();
                }
                if (m_firstName.endsWith(".")) {
                    m_firstName = m_firstName.substring(0, m_firstName.length() - 1);
                }
                m_name = getFullName(m_firstName, m_lastName);
            } else {
                pos = m_name.lastIndexOf(' ');
                if (pos >= 0) {
                    if (lastFirst) {
                        m_lastName = m_name.substring(0, pos).trim();
                        m_firstName = m_name.substring(pos).trim();
                    } else {
                        m_firstName = m_name.substring(0, pos).trim();
                        m_lastName = m_name.substring(pos).trim();
                    }
                    if (m_firstName.endsWith(".")) {
                        m_firstName = m_firstName.substring(0, m_firstName.length() - 1);
                    }
                    m_name = getFullName(m_firstName, m_lastName);
                } else {
                    m_lastName = m_name;
                    m_firstName = null;
                }
            }
        }
    }

    public BookAuthor(final String firstName, final String lastName) {
        m_firstName = LengthUtils.safeString(firstName).trim();
        m_lastName = LengthUtils.safeString(lastName).trim();
        m_name = getFullName(firstName, lastName);
    }

    public final String getName() {
        return m_name;
    }

    public final String getFirstName() {
        return m_firstName;
    }

    public final String getLastName() {
        return m_lastName;
    }

    @Override
    public String toString() {
        return m_name;
    }

    public static String getFullName(final String firstName, final String lastName) {
        return (lastName + " " + firstName).trim();
    }

}
