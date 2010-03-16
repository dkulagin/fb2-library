package org.ak2.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class StreamUtils {

    private static final String EOL = System.getProperty("line.separator");

    public static String getText(final Reader reader) throws IOException {
        final StringBuilder buf = new StringBuilder();
        final BufferedReader in = new BufferedReader(reader);
        for (String s = in.readLine(); s != null; s = in.readLine()) {
            buf.append(s).append(EOL);
        }
        return buf.toString();
    }

    public static byte[] getBytes(final InputStream stream) throws IOException {
        final InputStream in = stream;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buf = new byte[8192];
        for (int length = in.read(buf); length != -1; length = in.read(buf)) {
            out.write(buf, 0, length);
        }
        return out.toByteArray();
    }

}
