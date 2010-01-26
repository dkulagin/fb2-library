package org.ak2.fb2.core.bookstore;

import java.io.File;

public interface IFileCachProvider {

    IFileCach openFileCach(final File folder);
}
