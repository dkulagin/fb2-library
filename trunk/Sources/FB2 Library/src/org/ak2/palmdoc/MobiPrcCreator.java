package org.ak2.palmdoc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.book.image.FictionBookImage;
import org.ak2.utils.ResourceUtils;
import org.ak2.utils.xpath.IXPathApi;
import org.ak2.utils.xpath.XPathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrei Komarovskikh / Reksoft
 * 
 */
public class MobiPrcCreator {
    private static final String FB2_2_HTML_RU_XSL = ResourceUtils.getPackageResource("/", MobiPrcCreator.class, "/xsl/fb2html-palmdoc.xsl");
    private static final String HTML_ENCODING = "windows-1251";

    public static File createFile(File bookFolder, String outputFileName, FictionBook fb) throws IOException, TransformerFactoryConfigurationError,
            TransformerException, XPathException {
        boolean compress = false;

        String databaseName = fb.getBookName();
        PilotDocRecord docRecord = null;
        RandomAccessFile outputFile = null;
        DocumentHeader docHeader = new DocumentHeader();

        
        Node doc = setImageIndexes(fb);
        
        byte[] bookContent = getBookContent(doc);
        docHeader.storyLen = bookContent.length;
        docRecord = new PilotDocRecord(docHeader.storyLen);
        docRecord.assign(bookContent, docHeader.storyLen);

        File f = null;
        // Create output file
        try {
            f = new File(outputFileName);
            
            f.getAbsoluteFile().getParentFile().mkdirs();
            f.delete();

            outputFile = new RandomAccessFile(f, "rw");

        } catch (IOException e1) {
            System.err.println("The destination file `" + outputFileName + "` could not be created.");
            return null;
        }
        docHeader.storyLen = docRecord.convertEOL();

        byte docBytes[] = new byte[docRecord.length()];

        System.arraycopy(docRecord.buf, 0, docBytes, 0, docRecord.length());

        // Create PalmDB header
        DatabaseHeader dbHeader = new DatabaseHeader();

        dbHeader.name = databaseName;
        dbHeader.setModificationDate();
        dbHeader.creatorID = DatabaseHeader.REAd;
        dbHeader.typeID = DatabaseHeader.TEXt;
        docHeader.version = compress ? (short) 2 : (short) 1;
        docHeader.recordSize = PilotDocRecord.DEF_LEN;
        docHeader.numRecords = (short) (docHeader.storyLen / PilotDocRecord.DEF_LEN);

        if (docHeader.numRecords * PilotDocRecord.DEF_LEN < docHeader.storyLen)
            docHeader.numRecords++;

        FictionBookImage[] images = fb.getImages();

        dbHeader.numRecords = (short) (docHeader.numRecords + 1 + images.length);
        dbHeader.write(outputFile);

        // Create index
        RecordIndex recordIndex[] = new RecordIndex[dbHeader.numRecords];
        recordIndex[0] = new RecordIndex();
        recordIndex[0].write(outputFile);

        for (int i1 = 1; i1 < dbHeader.numRecords; i1++) {
            recordIndex[i1] = new RecordIndex();
            recordIndex[i1].fileOffset = 0;
            recordIndex[i1].write(outputFile);
        }

        recordIndex[0].fileOffset = (int) outputFile.getFilePointer();
        docHeader.write(outputFile);

        int indexInArray = 0;

        // Write doc records
        for (int record = 1; record <= docHeader.numRecords; record++) {
            recordIndex[record].fileOffset = (int) outputFile.getFilePointer();
            int recordSize = docHeader.recordSize;
            if (recordSize + indexInArray > docBytes.length)
                recordSize = docBytes.length - indexInArray;
            docRecord.assign(docBytes, recordSize, indexInArray);
            indexInArray += recordSize;
            if (compress) {
                docRecord.compress();
            }
            outputFile.write(docRecord.buf);
        }
        // Write images

        int imageRecordIndex = docHeader.numRecords + 1;
        for (int imageIndex = 0; imageIndex < images.length; imageIndex++, imageRecordIndex++) {
            recordIndex[imageRecordIndex].fileOffset = (int) outputFile.getFilePointer();
            BufferedImage img = images[imageIndex].transformImage(300, 300, 64000);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(img, "bmp", output);
            output.flush();
            output.close();
            outputFile.write(output.toByteArray());
        }
        System.out.println("");

        // Update record index
        outputFile.seek(DatabaseHeader.getSize());
        for (int j3 = 0; j3 < recordIndex.length; j3++) {
            recordIndex[j3].uniqueID = 0x6f8000 + j3;
            recordIndex[j3].write(outputFile);
        }

        outputFile.close();
        return f;
    }

    
    protected static Node setImageIndexes(FictionBook fb) throws TransformerException, XPathException {
        Document document = fb.getDocument();
        
        Node doc = document.cloneNode(true);
        
        final NodeList iter2 = IXPathApi.Factory.newInstance().selectNodes(doc, "//image");
        for (int i = 0; i < iter2.getLength(); i++) {
            
            final Element element = (Element) iter2.item(i);
            String imageName = element.getAttribute("l:href");
            if (imageName.startsWith("#")) {
                imageName = imageName.substring(1);
            }
            final int index = fb.getImageIndex(imageName);
            if (index != -1) {
                element.setAttribute("prctype", "BMP");

                String indexString = "" + (1 + index);

                final int j = indexString.length();
                for (int k = 0; k < 5 - j; k++) {
                    indexString = "0" + indexString;
                }

                element.setAttribute("prcindex", indexString);
            }
        }
        return doc;
    }
    
    
    private static byte[] getBookContent(Node document) throws TransformerFactoryConfigurationError, TransformerException, UnsupportedEncodingException {
        
        final InputStream resourceAsStream = MobiPrcCreator.class.getResourceAsStream(FB2_2_HTML_RU_XSL);

        final StreamSource xslForHtml = new StreamSource(resourceAsStream);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final DOMSource fb2FileSource = new DOMSource(document);

        final StreamResult htmlFile = new StreamResult(output);

        final Transformer t = TransformerFactory.newInstance().newTransformer(xslForHtml);
        t.setOutputProperty("encoding", HTML_ENCODING);

        t.transform(fb2FileSource, htmlFile);

        final String content = new String(output.toByteArray(), HTML_ENCODING);


        final String contentFix = correctHREFs(content);
        
        
        byte[] bookContent = contentFix.getBytes(HTML_ENCODING);
        return bookContent;
    }
    
    protected static String correctHREFs(final String content) {
        final HashMap<String, String> hrefs = new HashMap<String, String>();
        int index = content.indexOf("<a name=\"", 0);

        while (index != -1) {
            String xrefName = content.substring(index + 9, content.indexOf("\"", index + 9));
            while (xrefName.startsWith("#")) {
                xrefName = xrefName.substring(1);
            }

            final int hrefIndex = index;

            final String xrefValue = generateXrefValue(hrefIndex);

            index = content.indexOf("<a name=\"", index + 10 + xrefName.length());

            hrefs.put(xrefName, xrefValue);
        }

        int maxIndex = 0;

        for (final String name : hrefs.keySet()) {
            final String searchPattern1 = "<a filepos=\"000000000\" href=\"#" + name + "\"";
            final String searchPattern2 = "<a href=\"#" + name + "\" filepos=\"000000000\"";

            int index1 = content.indexOf(searchPattern1);
            if (index1 == -1) {
                index1 = content.indexOf(searchPattern2);
            }
            if (index1 > maxIndex) {
                maxIndex = index1;
            }
        }

        String header = content.substring(0, maxIndex + 100);

        for (final String name : hrefs.keySet()) {
            final String value = hrefs.get(name);

            final String searchPattern1 = "<a filepos=\"000000000\" href=\"#" + name + "\"";
            final String searchPattern2 = "<a href=\"#" + name + "\" filepos=\"000000000\"";

            final String replaceString = "<A HREF=\"#" + name + "\" filepos=\"" + value + "\"";
            int index1 = header.indexOf(searchPattern1);
            if (index1 == -1) {
                index1 = header.indexOf(searchPattern2);
            }
            if (index1 > 0) {
                header = header.substring(0, index1) + replaceString
                        + header.substring(index1 + searchPattern1.length());
            }
        }

        return header + content.substring(maxIndex + 100);

    }
    
    private static String generateXrefValue(final int index) {
        final int idx = index;
        String result = "" + (idx);
        final int l = result.length();
        for (int i = 0; i < 9 - l; i++) {
            result = "0" + result;
        }
        return result;
    }
    
}