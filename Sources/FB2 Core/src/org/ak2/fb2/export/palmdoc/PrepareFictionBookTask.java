package org.ak2.fb2.export.palmdoc;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
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
import org.ak2.fb2.core.utils.FileUtils;
import org.ak2.fb2.export.palmdoc.impl.Palette;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

public class PrepareFictionBookTask {

    private final ExportParameters fieldParams;

    private final ExportContext fieldContext;

    public PrepareFictionBookTask(final ExportParameters params, final ExportContext context) {
        fieldParams = params;
        fieldContext = context;
    }

    public static int getWorkUnits() {
        return 5;
    }

    public void execute(final IOperationMonitor monitor) throws UnsynchronizedBookException, TransformerException,
            IOException, ParserConfigurationException, SAXException {

        monitor.subTask("Prepare fiction book...");

        boolean cached = load();
        monitor.worked(1);

        extractImages();
        monitor.worked(1);

        if (!cached) {
            updateImageLinks();
            monitor.worked(1);

            setImageIndexes();
            monitor.worked(1);

            save();
            monitor.worked(1);
        } else {
            monitor.worked(3);
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

    protected void updateImageLinks() throws TransformerException {
/*        
        Element documentElement = fieldContext.getDocument().getDocumentElement();
        final NodeIterator iter2 = XPathAPI.selectNodeIterator(documentElement, "//image");
        for (Node node = iter2.nextNode(); node != null; node = iter2.nextNode()) {
            final Element element = (Element) node;
            final String href = element.getAttribute("xlink:href");
            if (href.length() > 0) {
                //element.setAttribute("l:href", href);
            }
        }
*/
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
            final String targetFileName = FileUtils.getFileName(sourceImage.getImageFileName(), "bmp");
            final File targetFile = cach.getCachedFile(digest, targetFileName);
            if (!targetFile.exists()) {
                final BufferedImage bufferedImage = transformImage(sourceImage);
                if (bufferedImage != null) {
                    final FileOutputStream output = new FileOutputStream(targetFile);
                    ImageIO.write(bufferedImage, "bmp", output);
                    output.flush();
                    output.close();
                }
            }
            fieldContext.addImageFile(targetFile);
        }
    }

    protected void setImageIndexes() throws TransformerException {
        final NodeIterator iter2 = XPathAPI.selectNodeIterator(fieldContext.getDocument().getDocumentElement(), "//image");
        for (Node node = iter2.nextNode(); node != null; node = iter2.nextNode()) {
            final Element element = (Element) node;
            String imageName = element.getAttribute("l:href");
            if (imageName.startsWith("#")) {
                imageName = imageName.substring(1);
            }
            final int index = fieldContext.getBook().getImageIndex(imageName);
            if (index != -1) {
                element.setAttribute("prctype", "BMP");

                String indexString = "" + (1 + index);

                final int j = indexString.length();
                for (int i = 0; i < 5 - j; i++) {
                    indexString = "0" + indexString;
                }

                element.setAttribute("prcindex", indexString);
            }
        }
    }

    private static BufferedImage transformImage(final FictionBookImage image) {
        BufferedImage transformed = null;
        final BufferedImage original = image.getImage();

        if (original != null) {
            transformed = transformImage(original);
        }
        return transformed;
    }

    private static BufferedImage transformImage(final BufferedImage image) {
        double scale = Math.min((double) 300 / image.getWidth(), (double) 300 / image.getHeight());

        if (scale > 1) {
            scale = 1.0;
        }
        int width = (int) (Math.round(scale * image.getWidth()));
        int height = (int) (Math.round(scale * image.getHeight()));

        while (width * height > 64000) {
            scale = scale * 0.9;
            width = (int) (Math.round(scale * image.getWidth()));
            height = (int) (Math.round(scale * image.getHeight()));
        }

        final AffineTransformOp op1 = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale),
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        final BufferedImage scaledImage = op1.filter(image, new BufferedImage(width, height, image.getType()));

        Palette pal = Palette.getRainbowPalette();

        final IndexColorModel BINARY_COLOR_MODEL = new IndexColorModel(8, 256, pal.R, pal.G, pal.B, -1);

        final int w = scaledImage.getWidth();
        final int h = scaledImage.getHeight();

        final BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, BINARY_COLOR_MODEL);

        bufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

        return bufferedImage;
    }
}
