package org.ak2.utils.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Alexander Kasatkin
 * 
 */
public interface IFile {

    /**
     * @return the short file name
     */
    public String getName();

    /**
     * @return the full file name
     */
    public String getFullName();

    /**
     * @return the parent folder.
     */
    public IFolder getParent();

    /**
     * @return the real file
     */
    public File getRealFile();
    /**
     * @return the log date
     */
    public Date getDateTime();

    /**
     * Tests whether the file or directory exists.
     * 
     * @return <code>true</code> if and only if the file or directory denoted by this abstract pathname exists;
     *         <code>false</code> otherwise
     */
    public boolean exists();

    /**
     * Opens file to read.
     * 
     * @return an instance of the {@link InputStream} object
     * @throws IOException thrown on error
     */
    public InputStream open() throws IOException;
}
