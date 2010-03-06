package org.ak2.fb2.library.commands.parameters;

import java.io.File;

public class FileSystemParameter extends BaseParameter {

    private final boolean m_fileAccepted;

    private final boolean m_folderAccepted;

    public FileSystemParameter(String name, String desc, boolean folderAccepted, boolean fileAccepted) {
        super(name, desc, folderAccepted ? new File(".") : null);
        m_folderAccepted = folderAccepted;
        m_fileAccepted = fileAccepted;
    }

    /**
     * @return the fileAccepted
     */
    public final boolean isFileAccepted() {
        return m_fileAccepted;
    }

    /**
     * @return the folderAccepted
     */
    public final boolean isFolderAccepted() {
        return m_folderAccepted;
    }
}
