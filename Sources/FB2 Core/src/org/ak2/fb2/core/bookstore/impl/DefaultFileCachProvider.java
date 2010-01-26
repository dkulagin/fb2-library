/**
 *
 */
package org.ak2.fb2.core.bookstore.impl;

import java.io.File;

import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.bookstore.IFileCachProvider;

/**
 * @author Alexander Kasatkin
 *
 */
public class DefaultFileCachProvider implements IFileCachProvider {

    /**
     * @see org.ak2.fb2.core.bookstore.IFileCachProvider#openFileCach(java.io.File)
     */
    public IFileCach openFileCach(final File folder) {
        return new DefaultFileCach(folder);
    }

}
