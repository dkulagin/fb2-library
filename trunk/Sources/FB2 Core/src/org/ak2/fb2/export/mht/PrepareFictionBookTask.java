package org.ak2.fb2.export.mht;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.bookstore.exceptions.UnsynchronizedBookException;
import org.ak2.fb2.core.fictionbook.FictionBook;
import org.ak2.fb2.core.fictionbook.image.FictionBookImage;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PrepareFictionBookTask {

    private final ExportParameters fieldParams;

    private final ExportContext fieldContext;

    public PrepareFictionBookTask(final ExportParameters params, final ExportContext context) {
        fieldParams = params;
        fieldContext = context;
    }

    public static int getWorkUnits() {
        return 3;
    }

    public void execute(final IOperationMonitor monitor) throws UnsynchronizedBookException, TransformerException,
            IOException, ParserConfigurationException, SAXException {

        monitor.subTask("Prepare fiction book...");

        boolean cached = load();
        monitor.worked(1);

        extractImages();
        monitor.worked(1);

        if (!cached) {
            save();
            monitor.worked(1);
        } else {
            monitor.worked(getWorkUnits() - 2);
        }
    }

    protected boolean load() throws UnsynchronizedBookException {
        FictionBook fictionBook = null;
        Document document = null;
        final File cachedFile = fieldParams.getCachedXmlFile();
        final boolean cached = cachedFile != null && cachedFile.exists();
        if (cached) {
            fictionBook = new FictionBook(new FileSource(cachedFile));
            document = fictionBook.getDocument();
        } else {
            fictionBook = fieldParams.getDescriptor().load();
            document = (Document) fictionBook.getDocument().cloneNode(true);

        }
        fieldContext.setBook(fictionBook);
        fieldContext.setDocument(document);
        return cached;
    }

    protected void save() throws TransformerFactoryConfigurationError, TransformerException {
        final File cachedFile = fieldParams.getCachedXmlFile();
        if (cachedFile != null && !cachedFile.exists()) {
            final Document document = fieldContext.getDocument();
            final DOMSource source = new DOMSource(document);
            final StreamResult result = new StreamResult(cachedFile);

            final Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(source, result);
        }
    }

    protected void extractImages() throws IOException {
        final IFileCach cach = fieldParams.getCach();
        final String digest = fieldParams.getDescriptor().getDigest();
        final FictionBookImage[] images = fieldContext.getBook().getImages();

        for (int i = 0; i < images.length; i++) {
            final FictionBookImage sourceImage = images[i];
            final String sourceFileName = sourceImage.getImageFileName();
            final File sourceFile = cach.getCachedFile(digest, sourceFileName);
            if (!sourceFile.exists()) {
                sourceImage.save(sourceFile);
            }
            fieldContext.addImageFile(sourceFile);
        }
    }
}
