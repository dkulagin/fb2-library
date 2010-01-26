package org.ak2.fb2.export.palmdoc.impl;

import java.io.DataOutput;
import java.io.IOException;

public class RecordIndex {
    public static final byte PRIVATE = 16;

    public byte attribute;

    public int fileOffset;

    public int uniqueID;

    public RecordIndex() {
        fileOffset = -1;
        attribute = 0x40;
        uniqueID = 0;
    }

    public void write(final DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(fileOffset);
        dataoutput.write(attribute);
        dataoutput.write((byte) (uniqueID >> 16));
        dataoutput.write((byte) (uniqueID >> 8));
        dataoutput.write((byte) uniqueID);
    }

    public static int getSize() {
        return 8;
    }
}