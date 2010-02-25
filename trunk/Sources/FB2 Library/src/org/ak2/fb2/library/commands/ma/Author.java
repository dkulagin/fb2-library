package org.ak2.fb2.library.commands.ma;

import java.io.File;

import org.ak2.fb2.library.book.BookAuthor;

public class Author extends BookAuthor{

    private final File m_folder;

    public Author(final String name, final File folder) {
        super(name);
        m_folder = folder;
    }

    public File getFolder() {
        return m_folder;
    }

    @Override
    public String toString() {
        return "Author [m_name=" + getName() + ", m_lastName=" + getLastName() + ", m_firstName=" +  getFirstName() + ", m_folder=" + m_folder + "]";
    }
}
