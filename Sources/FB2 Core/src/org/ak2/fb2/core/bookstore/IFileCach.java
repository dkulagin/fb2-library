package org.ak2.fb2.core.bookstore;

import java.io.File;

public interface IFileCach {

    File getCachedFile(final String ownerDigest, final String fileName);

}
