package org.ak2.fb2.export.palmdoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.export.palmdoc.impl.DatabaseHeader;
import org.ak2.fb2.export.palmdoc.impl.DocumentHeader;
import org.ak2.fb2.export.palmdoc.impl.PilotDocRecord;
import org.ak2.fb2.export.palmdoc.impl.RecordIndex;

public class GeneratePrcTask {

    private final ExportParameters fieldParams;

    private final ExportContext fieldContext;

    public GeneratePrcTask(final ExportParameters params, final ExportContext context) {
        fieldParams = params;
        fieldContext = context;
    }

    public static int getWorkUnits() {
        return 5;
    }

    public void execute(final IOperationMonitor monitor) throws IOException {
        monitor.subTask("Generate prc...");

        final File f = new File(fieldParams.getResultFileName());
        f.getParentFile().mkdirs();
        f.delete();
        final RandomAccessFile outputFile = new RandomAccessFile(fieldParams.getResultFileName(), "rw");

        monitor.worked(1);

        final byte[] content = fieldContext.getContent().getBytes(fieldParams.getHtmlEncoding());

        DocumentHeader docHeader = new DocumentHeader();
        docHeader.storyLen = content.length;

        PilotDocRecord docRecord = new PilotDocRecord(docHeader.storyLen);
        docRecord.assign(content, docHeader.storyLen);

        docHeader.storyLen = docRecord.convertEOL();

        final byte docBytes[] = new byte[docRecord.length()];

        System.arraycopy(docRecord.buf, 0, docBytes, 0, docRecord.length());

        // Create PalmDB header
        DatabaseHeader dbHeader = new DatabaseHeader(fieldParams.getDescriptor().getTitle());

        docHeader.version = fieldParams.isCompressPalmDoc() ? (short) 2 : (short) 1;
        docHeader.recordSize = PilotDocRecord.DEF_LEN;
        docHeader.numRecords = (short) (docHeader.storyLen / PilotDocRecord.DEF_LEN);

        if (docHeader.numRecords * PilotDocRecord.DEF_LEN < docHeader.storyLen) {
            docHeader.numRecords++;
        }

        dbHeader.numRecords = (short) (docHeader.numRecords + 1 + fieldContext.getImageFileCount());
        dbHeader.write(outputFile, fieldParams.getHtmlEncoding());

        monitor.worked(1);

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

        monitor.worked(1);

        // Write doc records
        int indexInArray = 0;
        for (int record = 1; record <= docHeader.numRecords; record++) {
            recordIndex[record].fileOffset = (int) outputFile.getFilePointer();
            int recordSize = docHeader.recordSize;
            if (recordSize + indexInArray > docBytes.length) {
                recordSize = docBytes.length - indexInArray;
            }
            docRecord.assign(docBytes, recordSize, indexInArray);
            indexInArray += recordSize;
            if (fieldParams.isCompressPalmDoc()) {
                docRecord.compress();
            }
            outputFile.write(docRecord.buf);
        }

        monitor.worked(1);

        // Write images
        final byte[] buf = new byte[70000];
        final File[] images = fieldContext.getImageFiles();
        int imageRecordIndex = docHeader.numRecords + 1;
        for (int imageIndex = 0; imageIndex < images.length; imageIndex++, imageRecordIndex++) {
            recordIndex[imageRecordIndex].fileOffset = (int) outputFile.getFilePointer();
            final FileInputStream fis = new FileInputStream(images[imageIndex]);
            final int bytesRead = fis.read(buf);
            outputFile.write(buf, 0, bytesRead);
            fis.close();
        }
        // Update record index
        outputFile.seek(DatabaseHeader.getSize());
        for (int j3 = 0; j3 < recordIndex.length; j3++) {
            recordIndex[j3].uniqueID = 0x6f8000 + j3;
            recordIndex[j3].write(outputFile);
        }

        outputFile.close();

        monitor.worked(1);
    }
}
