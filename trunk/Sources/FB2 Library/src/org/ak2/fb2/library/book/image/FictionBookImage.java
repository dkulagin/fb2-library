package org.ak2.fb2.library.book.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.ak2.utils.base64.Base64;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.w3c.dom.Element;

public class FictionBookImage {

    private String fieldImageFileName;

    private String fieldImageMineType;

    private byte[] fieldOriginal;

    private WeakReference<BufferedImage> fieldImage;

    public FictionBookImage(final Element imageElement) {
        fieldImageFileName = imageElement.getAttribute("id");
        fieldImageMineType = imageElement.getAttribute("content-type");

        String encoded = imageElement.getFirstChild().getNodeValue();

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
        Iterator<?> readersIterator = ImageIO.getImageReadersByMIMEType(fieldImageMineType);

        if (readersIterator.hasNext()) {
            ImageReader reader = (ImageReader) readersIterator.next();

            reader.setInput(new MemoryCacheImageInputStream(new ByteArrayInputStream(fieldOriginal)), true, true);

            try {
                BufferedImage image = reader.read(0);
                return image;
            } catch (Throwable th) {
                new JLogMessage(JLogLevel.ERROR, "Fiction book image {0} cannot be decoded").log(th, getImageFileName());
            }
        }
        return null;
    }
    
    public void save(final File imageFile) throws IOException {
        FileOutputStream out = new FileOutputStream(imageFile);
        out.write(fieldOriginal);
        out.close();
    }
}
