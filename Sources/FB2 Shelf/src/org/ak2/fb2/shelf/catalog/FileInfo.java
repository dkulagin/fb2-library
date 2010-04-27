package org.ak2.fb2.shelf.catalog;

import java.io.File;
import java.io.IOException;

import org.ak2.utils.FileUtils;

public class FileInfo {

    private final File m_location;
    private final File m_container;
    private File m_book;
    private String m_locationPath;
    private String m_containerPath;
    private String m_bookPath;

    public FileInfo(final String location, final String container, final String file) {
        m_location = new File(location);
        m_container = new File(m_location, container);
        m_bookPath = file;
    }

    public File getLocation() {
        return m_location;
    }

    public File getContainer() {
        return m_container;
    }

    public File getBook() {
        if (m_book == null) {
            m_book = m_container;
            if (m_container.exists() && m_container.isDirectory()) {
                m_book = new File(m_container, m_bookPath);
                m_bookPath = null;
            }
        }
        return m_book;
    }

    public String getLocationPath() {
        if (m_locationPath == null) {
            try {
                m_locationPath = getLocation().getCanonicalPath();
            } catch (IOException ex) {
                m_locationPath = getLocation().getAbsolutePath();
            }
        }
        return m_locationPath;
    }

    public String getContainerPath() {
        if (m_containerPath == null) {
            m_containerPath = FileUtils.getRelativeFileName(getContainer(), getLocation());
        }
        return m_containerPath;
    }

    public String getFullContainerPath() {
        try {
            return m_container.getCanonicalPath();
        } catch (IOException ex) {
            return m_container.getAbsolutePath();
        }
    }

    public String getBookPath() {
        if (m_bookPath == null) {
            m_bookPath = FileUtils.getRelativeFileName(getBook(), getContainer());
        }
        return m_bookPath;
    }

    public String getFullBookPath() {
        try {
            return m_book.getCanonicalPath();
        } catch (IOException ex) {
            return m_book.getAbsolutePath();
        }
    }

    @Override
    public String toString() {
        return "FileInfo [m_location=" + m_location + ", m_container=" + m_container + ", m_file=" + m_bookPath + "]";
    }


}
