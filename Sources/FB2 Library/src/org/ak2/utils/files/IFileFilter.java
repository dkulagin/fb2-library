package org.ak2.utils.files;

/**
 * @author Alexander Kasatkin
 * 
 */
public interface IFileFilter {

    /**
     * Checks the given file
     * 
     * @param file file to check
     * @return <code>true</code> if file was accepted
     */
    boolean accept(IFile file);
}
