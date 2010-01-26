/**
 *
 */
package org.ak2.fb2.core.bookstore.impl;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ak2.fb2.bookstore.impl.xsd.AuthorType;
import org.ak2.fb2.bookstore.impl.xsd.DateType;
import org.ak2.fb2.bookstore.impl.xsd.SequenceType;
import org.ak2.fb2.bookstore.impl.xsd.TArchivedFile;
import org.ak2.fb2.bookstore.impl.xsd.TFictionBookDescriptor;
import org.ak2.fb2.bookstore.impl.xsd.TFictionBookDescriptor.Description;
import org.ak2.fb2.bookstore.impl.xsd.TFictionBookDescriptor.Description.TitleInfo;
import org.ak2.fb2.bookstore.impl.xsd.TFictionBookDescriptor.Description.TitleInfo.Author;
import org.ak2.fb2.bookstore.impl.xsd.TFictionBookDescriptor.Description.TitleInfo.Genre;
import org.ak2.fb2.core.bookstore.IBook;
import org.ak2.fb2.core.bookstore.exceptions.UnsynchronizedBookException;
import org.ak2.fb2.core.fictionbook.FictionBook;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;
import org.ak2.fb2.core.fictionbook.source.ZipEntrySource;
import org.apache.xmlbeans.XmlString;

/**
 * @author Alexander Kasatkin
 *
 */
class XmlBook implements IBook {

    private final TFictionBookDescriptor fieldDescriptor;

    private WeakReference<FictionBook> fieldBook;

    /**
     * The Constructor.
     *
     * @param descriptor the book descriptor
     */
    XmlBook(final TFictionBookDescriptor descriptor) {
        this(descriptor, null);
    }

    /**
     * The Constructor.
     *
     * @param descriptor the book descriptor
     */
    XmlBook(final TFictionBookDescriptor descriptor, final FictionBook fb) {
        fieldDescriptor = descriptor;
        if (fb != null) {
            fieldBook = new WeakReference<FictionBook>(fb);
        }
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getSource()
     */
    public IFictionBookSource getSource() {
        final TFictionBookDescriptor book = getBook();
        if (book.isSetArchivedFile()) {
            final TArchivedFile archivedFile = book.getArchivedFile();
            return new ZipEntrySource(archivedFile.getArchive(), archivedFile.getStringValue());
        }
        if (book.isSetFile()) {
            return new FileSource(book.getFile());
        }
        return null;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#load()
     */
    public FictionBook load() throws UnsynchronizedBookException {
        FictionBook book = fieldBook != null ? fieldBook.get() : null;
        if (book == null) {
            book = new FictionBook(getSource());
            if (!getDigest().equals(book.getDigest())) {
                throw new UnsynchronizedBookException(getSource());
            }
            fieldBook = new WeakReference<FictionBook>(book);
        }
        return book;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getDigest()
     */
    public String getDigest() {
        return getBook().getDigest();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getGenres()
     */
    public String[] getGenres() {
        final List<Genre> genreList = getTitleInfo().getGenreList();
        final List<String> result = new ArrayList<String>(genreList.size());
        for (final Genre genre : genreList) {
            result.add(getStringValue(genre));
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getAuthors()
     */
    public String[] getAuthors() {
        final List<Author> authorList = getTitleInfo().getAuthorList();
        final List<String> result = new ArrayList<String>(authorList.size());
        for (final Author author : authorList) {
            final StringBuilder buf = new StringBuilder();
            buf.append(getStringValue(author.getFirstName()));
            final String middleName = getStringValue(author.getMiddleName());
            if (middleName != null && middleName.length() > 0) {
                buf.append(' ');
                buf.append(middleName);
            }
            buf.append(' ');
            buf.append(getStringValue(author.getLastName()));

            result.add(buf.toString());
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getTitle()
     */
    public String getTitle() {
        return getTitleInfo().getBookTitle().getStringValue();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getAnnotation()
     */
    public String getAnnotation() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getKeywords()
     */
    public String getKeywords() {
        final TitleInfo titleInfo = getTitleInfo();
        return getStringValue(titleInfo.getKeywords());
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getDate()
     */
    public Date getDate() {
        final DateType date = getTitleInfo().getDate();
        return date != null ? date.getValue().getTime() : null;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getLanguage()
     */
    public String getLanguage() {
        return getTitleInfo().getLang();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getOriginalLanguage()
     */
    public String getOriginalLanguage() {
        return getTitleInfo().getSrcLang();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getTranslators()
     */
    public String[] getTranslators() {
        final List<AuthorType> translatorList = getTitleInfo().getTranslatorList();
        final List<String> result = new ArrayList<String>(translatorList.size());
        for (final AuthorType author : translatorList) {
            final StringBuilder buf = new StringBuilder();
            buf.append(getStringValue(author.getFirstName()));
            final String middleName = getStringValue(author.getMiddleName());
            if (middleName != null && middleName.length() > 0) {
                buf.append(' ');
                buf.append(middleName);
            }
            buf.append(' ');
            buf.append(getStringValue(author.getLastName()));

            result.add(buf.toString());
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#isBelongToSequence()
     */
    public boolean isBelongToSequence() {
        final TitleInfo titleInfo = getTitleInfo();
        return !titleInfo.getSequenceList().isEmpty();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getSequenceName()
     */
    public String getSequenceName() {
        final TitleInfo titleInfo = getTitleInfo();
        final List<SequenceType> sequenceList = titleInfo.getSequenceList();
        if (sequenceList.isEmpty()) {
            return null;
        }
        return sequenceList.get(0).getName();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBook#getOrginalNumber()
     */
    public BigInteger getOrginalNumber() {
        final TitleInfo titleInfo = getTitleInfo();
        final List<SequenceType> sequenceList = titleInfo.getSequenceList();
        if (sequenceList.isEmpty()) {
            return null;
        }
        return sequenceList.get(0).getNumber();
    }

    /**
     * Gets a book.
     *
     * @return an instance of the {@link TFictionBookDescriptor} object or <code>null</code>
     */
    protected TFictionBookDescriptor getBook() {
        return fieldDescriptor;
    }

    /**
     * Gets the book description.
     *
     * @return an instance of the {@link Description} object
     */
    protected Description getDescription() {
        final TFictionBookDescriptor book = getBook();
        Description description = book.getDescription();
        if (description == null) {
            description = book.addNewDescription();
        }
        return description;
    }

    /**
     * Gets the title info.
     *
     * @return an instance of the {@link TitleInfo} object
     */
    protected TitleInfo getTitleInfo() {
        final Description description = getDescription();
        TitleInfo titleInfo = description.getTitleInfo();
        if (titleInfo == null) {
            titleInfo = description.addNewTitleInfo();
        }
        return titleInfo;
    }

    /**
     * Gets the string value of the given XML string.
     *
     * @param object the object
     *
     * @return the string value
     */
    private static String getStringValue(final XmlString object) {
        return getStringValue(object, null);
    }

    /**
     * Gets the string value of the given XML string.
     *
     * @param object the object
     * @param defaultValue the default value
     *
     * @return the string value
     */
    private static String getStringValue(final XmlString object, final String defaultValue) {
        return object != null ? object.getStringValue() : defaultValue;
    }
}
