package org.ak2.fb2.core.bookstore;

import java.math.BigInteger;
import java.util.Date;

import org.ak2.fb2.core.bookstore.exceptions.UnsynchronizedBookException;
import org.ak2.fb2.core.fictionbook.FictionBook;
import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;

public interface IBook {

    IFictionBookSource getSource();

    FictionBook load() throws UnsynchronizedBookException;

    String getDigest();

    String[] getGenres();

    String[] getAuthors();

    String getTitle();

    String getAnnotation();

    String getKeywords();

    Date getDate();

    String getLanguage();

    String getOriginalLanguage();

    String[] getTranslators();

    boolean isBelongToSequence();

    String getSequenceName();

    BigInteger getOrginalNumber();
}
