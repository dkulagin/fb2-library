package org.ak2.fb2.core.fictionbook.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.ak2.fb2.core.utils.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

public class FictionBookImage {

    private static final Log LOGGER = LogFactory.getLog(FictionBookImage.class);

    private final String fieldImageFileName;

    private final String fieldImageMineType;

    private final byte[] fieldOriginal;

    private WeakReference<BufferedImage> fieldImage;

    public FictionBookImage(final Element imageElement) {
        fieldImageFileName = imageElement.getAttribute("id");
        fieldImageMineType = imageElement.getAttribute("content-type");

        final String encoded = imageElement.getFirstChild().getNodeValue();

        fieldOriginal = Base64.decode(encoded);
    }

    /**
     * @return the imageFileName
     */
    public String getImageFileName() {
        return fieldImageFileName;
    }

    /**
     * @return the imageMineType
     */
    public String getImageMineType() {
        return fieldImageMineType;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        BufferedImage image = fieldImage != null ? fieldImage.get() : null;
        if (image == null) {
            image = readImage();
            fieldImage = new WeakReference<BufferedImage>(image);
        }
        return image;
    }

    private BufferedImage readImage() {
        final Iterator<?> readersIterator = ImageIO.getImageReadersByMIMEType(fieldImageMineType);

        if (readersIterator.hasNext()) {
            final ImageReader reader = (ImageReader) readersIterator.next();

            reader.setInput(new MemoryCacheImageInputStream(new ByteArrayInputStream(fieldOriginal)), true, true);

            try {
                final BufferedImage image = reader.read(0);
                return image;
            } catch (final Throwable th) {
                LOGGER.error("Fiction book image [" + getImageFileName() + "] cannot be decoded", th);
            }
        }
        return null;
    }

    public void save(final File imageFile) throws IOException {
        FileUtils.writeByteArrayToFile(imageFile, fieldOriginal);
    }
}
