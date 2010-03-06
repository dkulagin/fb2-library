package org.ak2.palmdoc;

import java.util.Arrays;

public class PilotDocRecord {
	public static final int COUNT_BITS = 3;

	public static final int DEF_LEN = 4096;

	public static final int DISP_BITS = 11;

	public static final int EOLCR = 2;

	public static final int EOLCRLF = 3;

	public static final int EOLLF = 1;

	public static final int EOLVM = 0;

	public static final int SHIFTED_COUNT_BITS_MINUS_1 = 7;

	public static final int SHIFTED_COUNT_BITS_PLUS_2 = 10;

	public static final int SHIFTED_DISP_BITS_MINUS_1 = 2047;

	public byte buf[];

	private byte destBuf[];

	private int destIndex;

	private int offsets[];

	private boolean space;

	public PilotDocRecord() {
		this(DEF_LEN);
	}

	public PilotDocRecord(int i) {
		buf = null;
		destBuf = null;
		destIndex = 0;
		space = false;
		offsets = new int[256];
		buf = new byte[i];
		if (i > DEF_LEN)
			i *= 2;
		else
			i = DEF_LEN * 2;
		destBuf = new byte[i * 2];
	}

	public void assign(byte bytes[], int length) {
		buf = new byte[length];
		System.arraycopy(bytes, 0, buf, 0, length);
	}

	public void assign(byte bytes[], int length, int pos) {
		buf = new byte[length];
		System.arraycopy(bytes, pos, buf, 0, length);
	}

	public int compress() {
		int j = 0;
		int k = 0;
		int l = 1;
		int i1 = buf.length;
		space = false;
		destIndex = 0;
		while (k != i1) {
			if (k - j > SHIFTED_DISP_BITS_MINUS_1)
				j = k - SHIFTED_DISP_BITS_MINUS_1;
			int i = findPatternBoyerMoore(k, l, j);
			if (i == -1 || i == k || l - k > 10 || l == i1) {
				if (l - k < 4) {
					issue(buf[k]);
					k++;
				} else {
					if (space) {
						destBuf[destIndex++] = 32;
						space = false;
					}
					int j1 = k - j;
					int l1 = ((j1 << 3) + l) - k - 4;
					destBuf[destIndex++] = (byte) (128 + (l1 >> 8));
					destBuf[destIndex++] = (byte) (l1 & 0xff);
					k = l - 1;
				}
				j = 0;
			} else {
				j = i;
			}
			if (l == i1)
				l--;
			l++;
		}
		if (space)
			destBuf[destIndex++] = 32;
		int k1;
		for (int i2 = k1 = 0; i2 < destIndex;) {
			destBuf[k1] = destBuf[i2];
			int j2 = destBuf[k1] & 0xff;
			if (j2 >= 128 && j2 < 192)
				destBuf[++k1] = destBuf[++i2];
			else if (destBuf[k1] == 1) {
				for (destBuf[k1 + 1] = destBuf[i2 + 1]; i2 + 2 < destIndex
						&& destBuf[i2 + 2] == 1 && destBuf[k1] < 8; i2 += 2) {
					destBuf[k1]++;
					destBuf[k1 + destBuf[k1]] = destBuf[i2 + 3];
				}

				k1 += destBuf[k1];
				i2++;
			}
			i2++;
			k1++;
		}

		assign(destBuf, k1);
		return k1;
	}

	public int convertEOL() {
		for (int i = destIndex = 0; i < buf.length;) {
			destBuf[destIndex] = buf[i];
/*			
			if (destBuf[destIndex] == 13)
				if (i < buf.length - 1 && buf[i + 1] == 10)
					destIndex--;
				else
					destBuf[destIndex] = 10;
*/					
			i++;
			destIndex++;
		}

		assign(destBuf, destIndex);
		return destIndex;
	}

	public int convertEOL(int i) {
		byte abyte0[] = null;
		if (i == 0) {
			String s = System.getProperty("line.separator");
			abyte0 = s.getBytes();
		} else if (i == 2)
			abyte0 = (new byte[] { 13 });
		else if (i == 1)
			abyte0 = (new byte[] { 10 });
		else if (i == 3)
			abyte0 = (new byte[] { 13, 10 });
		for (int j = destIndex = 0; j < buf.length;) {
			destBuf[destIndex] = buf[j];
			if (destBuf[destIndex] == 10) {
				int k;
				for (k = 0; k < abyte0.length; k++)
					destBuf[destIndex + k] = abyte0[k];

				destIndex = (destIndex + k) - 1;
			}
			j++;
			destIndex++;
		}

		assign(destBuf, destIndex);
		return destIndex;
	}

	public int decompress() {
		for (int i = destIndex = 0; i < buf.length;) {
			int j = buf[i++] & 0xff;
			if (j > 0 && j < 9) {
				System.arraycopy(buf, i, destBuf, destIndex, j);
				i += j;
			} else if (j < 128)
				destBuf[destIndex++] = (byte) j;
			else if (j >= 192) {
				destBuf[destIndex++] = 32;
				destBuf[destIndex++] = (byte) (j ^ 0x80);
			} else {
				j = j << 8 | buf[i++] & 0xff;
				int k = (j & 0x3fff) >> 3;
				int l = (j & 7) + 3;
				while (l-- > 0) {
					destBuf[destIndex] = destBuf[destIndex - k];
					destIndex++;
				}
			}
		}

		assign(destBuf, destIndex);
		return destIndex;
	}

	/*
	 * private int findPattern(int i, int j, int k) { int l = j - i; for (; k <
	 * buf.length; k++) { if (buf[k] != buf[i]) continue; int i1; for (i1 = 1;
	 * i1 < l && buf[k + i1] == buf[i + i1]; i1++); if (i1 == l) return k; }
	 * 
	 * return -1; }
	 */
	
	private int findPatternBoyerMoore(int i, int j, int k) {
		int l = j - i;
		if (l == 0)
			return k;
		Arrays.fill(offsets, l);
		int i1 = l - 1;
		for (int j1 = 0; j1 < l;) {
			offsets[buf[i + j1] + 128] = i1;
			j1++;
			i1--;
		}

		k += l;
		i1 = l;
		int k1 = l - 1;
		int l1 = buf.length;
		do {
			k--;
			for (i1--; buf[k] != buf[i + i1]; i1 = k1) {
				int i2 = offsets[buf[k] + 128];
				int j2 = l - i1;
				k += j2 <= i2 ? i2 : j2;
				if (k >= l1)
					return -1;
			}

		} while (i1 > 0);
		return k;
	}

	private void issue(byte byte0) {
		byte byte1 = byte0;
		if (space) {
			if (byte1 >= 64 && byte1 <= 127) {
				destBuf[destIndex++] = (byte) (byte1 ^ 0x80);
			} else {
				destBuf[destIndex++] = 32;
				if (byte1 < 128 && (byte1 == 0 || byte1 > 8)) {
					destBuf[destIndex++] = byte0;
				} else {
					destBuf[destIndex++] = 1;
					destBuf[destIndex++] = byte0;
				}
			}
			space = false;
		} else if (byte1 == 32)
			space = true;
		else if (byte1 < 128 && (byte1 == 0 || byte1 > 8)) {
			destBuf[destIndex++] = byte0;
		} else {
			destBuf[destIndex++] = 1;
			destBuf[destIndex++] = byte0;
		}
	}

	public int length() {
		return buf.length;
	}

	public int removeBinary() {
		for (int i = destIndex = 0; i < buf.length;) {
			destBuf[destIndex] = buf[i];
			if (destBuf[destIndex] >= 0 && destBuf[i] < 9)
				destIndex--;
			i++;
			destIndex++;
		}

		assign(destBuf, destIndex);
		return destIndex;
	}

	public int unwrap(int i) {
		int j = -1;
		for (int i1 = destIndex = 0; i1 < buf.length;) {
			destBuf[destIndex] = buf[i1];
			if (destBuf[destIndex] == 10) {
				boolean flag = false;
				if (!flag) {
					flag = true;
					int k = j + 1;
					int l = destIndex - 1;
					do {
						if (k >= l)
							break;
						if (destBuf[k] != destBuf[l]) {
							flag = false;
							break;
						}
						k++;
						l--;
					} while (true);
					if (flag && j > -1)
						destBuf[j] = 10;
				}
				if (!flag && destIndex - j > i)
					if (destBuf[destIndex - 1] == 32
							|| destBuf[destIndex - 1] == 10)
						destIndex--;
					else
						destBuf[destIndex] = 32;
				j = destIndex;
			}
			i1++;
			destIndex++;
		}

		assign(destBuf, destIndex);
		return destIndex;
	}
}