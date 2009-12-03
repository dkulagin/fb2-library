package org.ak2.utils.files;

/**
 * @author Alexander Kasatkin
 * 
 */
public interface IFolder extends IFile {

    /**
     * Enumerates files in the folder.
     * 
     * @param filter file filter
     * @param options scanner options
     */
    public void enumerate(IFileFilter filter, FileScanner.Options options);

}
