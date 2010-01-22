package org.ak2.palmdoc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.ak2.fb2.library.book.FictionBook;
import org.ak2.fb2.library.book.image.FictionBookImage;

/**
 * @author Andrei Komarovskikh / Reksoft
 * 
 */
public class MobiPrcCreator {

    public static File createFile(File bookFolder, String outputFileName, FictionBook fb) throws IOException, TransformerFactoryConfigurationError,
            TransformerException {
        boolean compress = false;

        System.out.println("Generating PRC...");
        String databaseName = fb.getBookName();
        PilotDocRecord docRecord = null;
        RandomAccessFile outputFile = null;
        DocumentHeader docHeader = new DocumentHeader();

        docHeader.storyLen = fb.getBytes().length;
        docRecord = new PilotDocRecord(docHeader.storyLen);
        docRecord.assign(fb.getBytes(), docHeader.storyLen);

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
        System.out.print("Writing records:");

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
            System.out.print(" " + record);
            outputFile.write(docRecord.buf);
        }

        System.out.println("");
        // Write images

        // Iterator it = images.iterator();

        System.out.print("Writing images:");

        int imageRecordIndex = docHeader.numRecords + 1;
        for (int imageIndex = 0; imageIndex < images.length; imageIndex++, imageRecordIndex++) {
            recordIndex[imageRecordIndex].fileOffset = (int) outputFile.getFilePointer();
            System.out.print(" " + (imageIndex + 1));
            outputFile.write(images[imageIndex].getRawData());
        }
        System.out.println("");

        // Update record index
        outputFile.seek(DatabaseHeader.getSize());
        for (int j3 = 0; j3 < recordIndex.length; j3++) {
            recordIndex[j3].uniqueID = 0x6f8000 + j3;
            recordIndex[j3].write(outputFile);
        }

        outputFile.close();
        System.out.println("PRC Generation finished");
        return f;
    }
}