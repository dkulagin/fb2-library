package org.ak2.fb2.core.bookstore.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ak2.fb2.bookstore.impl.xsd.FictionBookStoreDocument;
import org.ak2.fb2.bookstore.impl.xsd.TFictionBookDescriptor;
import org.ak2.fb2.core.bookstore.IBook;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.fictionbook.FictionBook;
import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;
import org.ak2.fb2.core.utils.ResourceUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

class XmlBookStore implements IBookStore {

    private static final String BOOKSTORE_FOLDER_NAME = ".bookstore";

    private static final String ENCODING = "UTF-8";

    private static final String XSL = ResourceUtils.getPackageResource("/", XmlBookStore.class, "/xsl/Base.xsl");

    private static final Log LOGGER = LogFactory.getLog(XmlBookStore.class);

    private FictionBookStoreDocument fieldDocument;

    private final File fieldStore;

    private final HashMap<String, IBook> fieldBooks = new HashMap<String, IBook>();

    /**
     * The Constructor.
     *
     * @param folder the folder
     */
    public XmlBookStore(final File folder) {
        fieldStore = new File(folder, BOOKSTORE_FOLDER_NAME);
        load();
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBookStore#exists()
     */
    public boolean exists() {
        load();
        return fieldDocument != null;
    }

    public void load() {
        if (fieldDocument == null) {
            final File folder = fieldStore.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (fieldStore.exists()) {
                openFile(fieldStore);
            } else {
                createFile(fieldStore);
            }

            if (fieldDocument != null) {
                validate(fieldDocument);
            }
        }
    }

    public void clean() {
        if (fieldStore != null) {
            try {
                fieldStore.delete();
                FileUtils.deleteDirectory(fieldStore.getParentFile());
            } catch (final IOException ex) {
                LOGGER.error("Bookstore cleaning failed", ex);
            }
            fieldDocument = null;
            fieldBooks.clear();
            LOGGER.debug("Bookstore ["+fieldStore.getAbsolutePath()+"] cleaned");
        }
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBookStore#addBook(java.io.File)
     */
    public int addBook(final IFictionBookSource source) {
        if (source == null || !source.exists()) {
            return -1;
        }

        load();

        final InputStream resourceAsStream = getClass().getResourceAsStream(XSL);
        final StreamSource xsl = new StreamSource(resourceAsStream);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final StreamResult descriptiorResult = new StreamResult(output);

        try {
            final FictionBook book = new FictionBook(source);

            book.transtorm(xsl, descriptiorResult);

            final String toString = output.toString(ENCODING);

            final TFictionBookDescriptor descriptor = TFictionBookDescriptor.Factory.parse(toString);

            final List<TFictionBookDescriptor> list = fieldDocument.getFictionBookStore().getFictionBookDescriptorList();
            list.add(descriptor);

            final int index = list.size() - 1;

            if (validate(fieldDocument)) {
                saveFile(fieldStore);
            } else {
                list.remove(index);
                return -1;
            }

            return index;
        } catch (final Exception e) {
            LOGGER.error("Adding book to bookstore failed", e);
        }
        return -1;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBookStore#getBookCount()
     */
    public int getBookCount() {
        load();
        return exists() ? fieldDocument.getFictionBookStore().sizeOfFictionBookDescriptorArray() : 0;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBookStore#getBook(int)
     */
    public IBook getBook(final int index) {
        load();

        final TFictionBookDescriptor descriptor = fieldDocument.getFictionBookStore().getFictionBookDescriptorArray(index);
        if (descriptor != null) {
            final String digest = descriptor.getDigest();
            IBook book = fieldBooks.get(digest);
            if (book == null) {
                book = new XmlBook(descriptor);
                fieldBooks.put(digest, book);
            }
            return book;
        }
        return null;
    }

    /**
     * @see org.ak2.fb2.core.bookstore.IBookStore#getBook(java.lang.String)
     */
    public IBook getBook(final String digest) {
        load();

        IBook book = fieldBooks.get(digest);
        if (book == null) {
            for (final TFictionBookDescriptor descriptor : fieldDocument.getFictionBookStore().getFictionBookDescriptorList()) {
                if (digest.equals(descriptor.getDigest())) {
                    book = new XmlBook(descriptor);
                    fieldBooks.put(digest, book);
                    break;
                }
            }
        }
        return book;
    }

    /**
     * Creates the storage file.
     *
     * @param store the storage file
     */
    private void createFile(final File store) {
        fieldDocument = FictionBookStoreDocument.Factory.newInstance();
        fieldDocument.addNewFictionBookStore();
        LOGGER.debug("Bookstore ["+fieldStore.getAbsolutePath()+"] created");
        try {
            saveFile(store);
        } catch (final IOException e) {
            LOGGER.error("Fiction book store cannot be created", e);
            fieldDocument = null;
        }
    }

    /**
     * Open a storage file.
     *
     * @param store the storage file
     */
    private void openFile(final File store) {
        try {
            fieldDocument = FictionBookStoreDocument.Factory.parse(store);
            LOGGER.debug("Bookstore ["+fieldStore.getAbsolutePath()+"] loaded");
        } catch (final XmlException e) {
            LOGGER.error("Fiction book store cannot be open", e);
        } catch (final IOException e) {
            LOGGER.error("Fiction book store cannot be open", e);
        }
    }

    /**
     * Save the storage file.
     *
     * @param store the storage file
     *
     * @throws IOException the IO exception
     */
    private void saveFile(final File store) throws IOException {
        final XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        options.setSaveAggressiveNamespaces();
        options.setSaveNamespacesFirst();
        options.setUseDefaultNamespace();
        options.setCharacterEncoding(ENCODING);

        fieldDocument.save(store, options);
        LOGGER.debug("Bookstore ["+fieldStore.getAbsolutePath()+"] saved");
    }

    /**
     * Validate the given XML object.
     *
     * @param xmlObject the xml object
     *
     * @return <code>true</code> if valid
     */
    private boolean validate(final XmlObject xmlObject) {
        final LinkedList<Object> errors = new LinkedList<Object>();
        final XmlOptions options = new XmlOptions();
        options.setErrorListener(errors);
        if (!xmlObject.validate(options)) {
            for (final Object object : errors) {
                System.err.println(object.toString());
            }
            return false;
        }
        return true;
    }
}
