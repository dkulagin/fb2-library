package org.ak2.palmdoc;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHeader {

	private static final long SecondsSince1904 = 0x7c25b080L;

	public static final int TEXt = 0x54455874;

	public static final int REAd = 0x52454164;

	public static int getSize() {
		return 78;
	}

	public int appInfoID;

	public short attribute;

	public int creationDate;

	public int creatorID;

	public int lastBackupDate;

	public int modificationDate;

	public int modificationNumber;

	public String name;

	public int nextRecordListID;

	public short numRecords;

	public int sortInfoID;

	public int typeID;

	public int uniqueIDSeed;

	public short version;

	public DatabaseHeader() {
		name = "";
		attribute = 0;
		version = 0;
		creationDate = 0;
		modificationDate = 0;
		lastBackupDate = 0;
		modificationNumber = 0;
		appInfoID = 0;
		sortInfoID = 0;
		typeID = 0;
		creatorID = 0;
		uniqueIDSeed = 0;
		nextRecordListID = 0;
		numRecords = 0;
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		creationDate = (int) ((date.getTime() + (long) calendar.get(15) + (long) calendar
				.get(16)) / 1000L + SecondsSince1904);
		modificationDate = creationDate;
	}

	public Date getCreationDate() {
		Calendar calendar = Calendar.getInstance();
		long l = (new Integer(creationDate)).longValue();
		if (l < 0L)
			l += 0x100000000L;
		return new Date((l - SecondsSince1904) * 1000L
				- (long) calendar.get(15) - (long) calendar.get(16));
	}

	public Date getModificationDate() {
		Calendar calendar = Calendar.getInstance();
		long l = (new Integer(modificationDate)).longValue();
		if (l < 0L)
			l += 0x100000000L;
		return new Date((l - SecondsSince1904) * 1000L
				- (long) calendar.get(15) - (long) calendar.get(16));
	}

	public void setModificationDate() {
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		modificationDate = (int) ((date.getTime() + (long) calendar.get(15) + (long) calendar
				.get(16)) / 1000L + SecondsSince1904);
	}

	public void write(DataOutput dataoutput) throws IOException {
		byte dbName[] = new byte[32];
		dbName[dbName.length - 1] = 0;
		byte tmpName[] = name.getBytes("windows-1251");
		for (int i = 0; i < dbName.length - 1; i++)
			if (i < tmpName.length)
				dbName[i] = tmpName[i];
			else
				dbName[i] = 0;

		if (tmpName.length >= dbName.length) {
			dbName[dbName.length - 2] = 46;
			dbName[dbName.length - 3] = 46;
			dbName[dbName.length - 4] = 46;
		}

		dataoutput.write(dbName);
		dataoutput.writeShort(attribute);
		dataoutput.writeShort(version);
		dataoutput.writeInt(creationDate);
		dataoutput.writeInt(modificationDate);
		dataoutput.writeInt(lastBackupDate);
		dataoutput.writeInt(modificationNumber);
		dataoutput.writeInt(appInfoID);
		dataoutput.writeInt(sortInfoID);
		dataoutput.writeInt(typeID);
		dataoutput.writeInt(creatorID);
		dataoutput.writeInt(uniqueIDSeed);
		dataoutput.writeInt(nextRecordListID);
		dataoutput.writeShort(numRecords);
	}

}