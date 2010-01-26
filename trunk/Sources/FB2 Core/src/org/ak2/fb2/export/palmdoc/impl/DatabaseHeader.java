package org.ak2.fb2.export.palmdoc.impl;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

public class DatabaseHeader {

	public static final int TEXt = 0x54455874;

	public static final int REAd = 0x52454164;

	public int appInfoID;

	public short attribute;

	public int fieldCreationDate;

	private final int fieldCreatorID;

	public int lastBackupDate;

	public int fieldModificationDate;

	public int modificationNumber;

	private final String fieldName;

	public int nextRecordListID;

	public short numRecords;

	public int sortInfoID;

	private final int fieldTypeID;

	public int uniqueIDSeed;

	public short version;

	public DatabaseHeader(final String name) {
		fieldName = name;
		attribute = 0;
		version = 0;
		lastBackupDate = 0;
		modificationNumber = 0;
		appInfoID = 0;
		sortInfoID = 0;

        fieldCreatorID = DatabaseHeader.REAd;
        fieldTypeID = DatabaseHeader.TEXt;

		uniqueIDSeed = 0;
		nextRecordListID = 0;
		numRecords = 0;

		fieldCreationDate = PalmDate.getPalmDate(new Date());
		fieldModificationDate = fieldCreationDate;
	}

	public Date getCreationDate() {
        return new Date(PalmDate.getDate(fieldCreationDate));
	}

	public Date getModificationDate() {
        return new Date(PalmDate.getDate(fieldModificationDate));
	}

	public void setModificationDate() {
        fieldModificationDate = PalmDate.getPalmDate(new Date());
	}

	public void write(final DataOutput dataoutput, final String encoding) throws IOException {
		final byte dbName[] = new byte[32];
		dbName[dbName.length - 1] = 0;
		final byte tmpName[] = fieldName.getBytes(encoding);
		for (int i = 0; i < dbName.length - 1; i++) {
            if (i < tmpName.length) {
                dbName[i] = tmpName[i];
            } else {
                dbName[i] = 0;
            }
        }

		if (tmpName.length >= dbName.length) {
			dbName[dbName.length - 2] = 46;
			dbName[dbName.length - 3] = 46;
			dbName[dbName.length - 4] = 46;
		}

		dataoutput.write(dbName);
		dataoutput.writeShort(attribute);
		dataoutput.writeShort(version);
		dataoutput.writeInt(fieldCreationDate);
		dataoutput.writeInt(fieldModificationDate);
		dataoutput.writeInt(lastBackupDate);
		dataoutput.writeInt(modificationNumber);
		dataoutput.writeInt(appInfoID);
		dataoutput.writeInt(sortInfoID);
		dataoutput.writeInt(fieldTypeID);
		dataoutput.writeInt(fieldCreatorID);
		dataoutput.writeInt(uniqueIDSeed);
		dataoutput.writeInt(nextRecordListID);
		dataoutput.writeShort(numRecords);
	}

    public static int getSize() {
        return 78;
    }
}