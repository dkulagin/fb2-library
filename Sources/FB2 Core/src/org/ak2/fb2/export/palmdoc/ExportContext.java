package org.ak2.fb2.export.palmdoc;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.ak2.fb2.core.fictionbook.FictionBook;
import org.w3c.dom.Document;

public class ExportContext {

    private FictionBook fieldBook;

    private Document fieldDocument;

    private String fieldContent;

    private final LinkedList<File> fieldImageFiles = new LinkedList<File>();

    private final HashMap<String, String> fieldHrefs = new HashMap<String, String>();

    public void release() {
        fieldBook = null;
        fieldDocument = null;
        fieldContent = null;

        fieldImageFiles.clear();
        fieldHrefs.clear();
    }

    /**
     * @return the book
     */
    public final FictionBook getBook() {
        return fieldBook;
    }

    /**
     * @return the content
     */
    public final String getContent() {
        return fieldContent;
    }

    /**
     * @return the document
     */
    public final Document getDocument() {
        return fieldDocument;
    }

    /**
     * @param book the book to set
     */
    public final void setBook(final FictionBook book) {
        fieldBook = book;
    }

    /**
     * @param content the content to set
     */
    public final void setContent(final String content) {
        fieldContent = content;
    }

    /**
     * @param document the document to set
     */
    public final void setDocument(final Document document) {
        fieldDocument = document;
    }

    public void addImageFile(final File imageFile) {
        fieldImageFiles.add(imageFile);
    }

    public int getImageFileCount() {
        return fieldImageFiles.size();
    }

    public File[] getImageFiles() {
        return fieldImageFiles.toArray(new File[fieldImageFiles.size()]);
    }

    /**
     * @return the hrefs
     */
    public final HashMap<String, String> getHrefs() {
        return fieldHrefs;
    }
}
