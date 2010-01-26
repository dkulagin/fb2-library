package org.ak2.fb2.core.fictionbook;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import org.ak2.fb2.core.fictionbook.image.FictionBookImage;
import org.ak2.fb2.core.fictionbook.source.FileSource;
import org.ak2.fb2.core.fictionbook.source.IFictionBookSource;
import org.ak2.fb2.core.fictionbook.source.ZipEntrySource;
import org.ak2.fb2.core.utils.Digest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * @author Whippet
 */
public class FictionBook {

    private static final String FICTION_BOOK_IMAGES = "/FictionBook/binary";

    private static final Log LOGGER = LogFactory.getLog(FictionBook.class);

    private final IFictionBookSource fieldSource;

    private Document fieldDocument;

    private String fieldDigest;

    private HashMap<String, FictionBookImage> fieldImages;

    /**
     * The Constructor.
     *
     * @param source the book source
     */
    public FictionBook(final IFictionBookSource source) {
        fieldSource = source;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public IFictionBookSource getSource() {
        return fieldSource;
    }

    /**
     * Gets the document.
     *
     * @return the document
     */
    public Document getDocument() {
        if (fieldDocument == null) {
            final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            try {
                final DocumentBuilder b = f.newDocumentBuilder();
                try {
                    final Digest digest = new Digest(fieldSource.createInputStream());
                    fieldDocument = b.parse(digest.getInputStream());
                    fieldDigest = digest.getDigest();
                } catch (final SAXException e) {
                    LOGGER.error("", e);
                } catch (final IOException e) {
                    LOGGER.error("", e);
                } catch (final NoSuchAlgorithmException e) {
                    LOGGER.error("", e);
                } finally {
                    if (fieldDocument == null) {
                        fieldDocument = b.newDocument();
                        fieldDigest = "";
                    }
                }
            } catch (final ParserConfigurationException e) {
                LOGGER.error("", e);
            }
        }
        return fieldDocument;
    }

    /**
     * Gets the digest.
     *
     * @return the digest
     */
    public String getDigest() {
        if (fieldDigest == null) {
            getDocument();
        }
        return fieldDigest;
    }

    public int getImageIndex(final String imageFileName) {
        final FictionBookImage[] images = getImages();
        for (int i = 0; i < images.length; i++) {
            final FictionBookImage image = images[i];
            if (image.getImageFileName().equals(imageFileName)) {
                return i;
            }
        }
        return -1;
    }

    public FictionBookImage getImage(final String imageFileName) {
        return getImageMap().get(imageFileName);
    }

    public String[] getImageFileNames() {
        final HashMap<String, FictionBookImage> imageMap = getImageMap();
        return imageMap.keySet().toArray(new String[imageMap.size()]);
    }

    public FictionBookImage[] getImages() {
        final HashMap<String, FictionBookImage> imageMap = getImageMap();
        return imageMap.values().toArray(new FictionBookImage[imageMap.size()]);
    }

    /**
     * Transtorm the document.
     *
     * @param result the transformation result
     * @param xsl the xsl
     *
     * @return an instance of the {@link Result} object
     *
     * @throws TransformerException the transformer exception
     * @throws TransformerFactoryConfigurationError the transformer factory configuration error
     */
    public Result transtorm(final Source xsl, final Result result) throws TransformerFactoryConfigurationError,
            TransformerException {

        final Source fb2Source = new DOMSource(getDocument());
        final Transformer t = TransformerFactory.newInstance().newTransformer(xsl);

        if (fieldSource instanceof ZipEntrySource) {
            final ZipEntrySource zipEntrySource = ((ZipEntrySource) fieldSource);
            t.setParameter("archiveName", zipEntrySource.getZipFileName());
            t.setParameter("fileName", zipEntrySource.getZipEntryName());
        } else if (fieldSource instanceof FileSource) {
            final FileSource fileSource = ((FileSource) fieldSource);
            t.setParameter("fileName", fileSource.getFile().getPath());
        }
        t.setParameter("digest", getDigest());

        t.transform(fb2Source, result);
        return result;
    }

    protected HashMap<String, FictionBookImage> getImageMap() {
        if (fieldImages == null) {
            fieldImages = new HashMap<String, FictionBookImage>();
            try {
                final Element root = fieldDocument.getDocumentElement();
                final NodeIterator iter = XPathAPI.selectNodeIterator(root, FICTION_BOOK_IMAGES);
                for (Node node = iter.nextNode(); node != null; node = iter.nextNode()) {
                    final Element element = (Element) node;
                    final FictionBookImage image = new FictionBookImage(element);
                    fieldImages.put(image.getImageFileName(), image);
                }
            } catch (final Throwable th) {
                LOGGER.error("", th);
            }
        }
        return fieldImages;
    }
}