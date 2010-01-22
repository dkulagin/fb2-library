package org.ak2.palmdoc;
import java.io.DataOutput;
import java.io.IOException;

public class DocumentHeader {
  public static final short COMPRESSED = 2;
  public static final short textRecordSize = 4096;

  public static final short UNCOMPRESSED = 1;

  public static int getSize() {
    return 16;
  }
  public short numRecords;
  public int position;
  public short recordSize;
  public short spare;
  public int storyLen;
  public short version;

  public DocumentHeader() {
    version = 0;
    spare = 0;
    storyLen = 0;
    numRecords = 0;
    recordSize = 4096;
    position = 0x10000000;
  }

  public boolean isCompressed() {
    return version == 2;
  }

  public void write(DataOutput dataoutput) throws IOException {
    dataoutput.writeShort(version);
    dataoutput.writeShort(spare);
    dataoutput.writeInt(storyLen);
    dataoutput.writeShort(numRecords);
    dataoutput.writeShort(recordSize);
    dataoutput.writeShort(0x00);
    for (int i = 0; i < 199; i++)
      dataoutput.write(0x0E);
    
    dataoutput.write(0x00);
  }
}