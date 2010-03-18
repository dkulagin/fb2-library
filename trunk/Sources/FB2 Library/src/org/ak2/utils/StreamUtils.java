package org.ak2.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StreamUtils {

    private static final String EOL = System.getProperty("line.separator");

    public static String getResourceAsText(final Class<?> clazz, final String resource, final String def) {
        final InputStream stream = clazz.getResourceAsStream(resource);
        if (stream != null) {
            try {
                return StreamUtils.getText(stream);
            } catch (final IOException ex) {
            }
        }
        return def;
    }

    public static String getText(final InputStream stream) throws IOException {
        return loadText(new InputStreamReader(stream), new TextLoader()).toString();
    }

    public static String getText(final Reader reader) throws IOException {
        return loadText(reader, new TextLoader()).toString();
    }

    public static <T> T loadText(final Reader reader, final ITextLoader<T> loader) throws IOException {
        try {
            loader.reset();
            final BufferedReader in = new BufferedReader(reader);
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                if (!loader.onLine(s)) {
                    break;
                }
            }
            return loader.getResult();
        } finally {
            close(reader);
        }
    }

    public static byte[] getBytes(final InputStream stream) throws IOException {
        try {
            final InputStream in = stream;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buf = new byte[64 * 1024];
            for (int length = in.read(buf); length != -1; length = in.read(buf)) {
                out.write(buf, 0, length);
            }
            return out.toByteArray();
        } finally {
            close(stream);
        }
    }

    public static void close(final Closeable c) {
        try {
            c.close();
        } catch (final Exception ex) {
        }
    }

    public static interface ITextLoader<T> {

        boolean onLine(String line);

        T getResult();

        void reset();
    }

    public abstract static class AsbstractTextLoader<T> implements ITextLoader<T> {

        protected final T m_result;

        protected AsbstractTextLoader(final T empty) {
            m_result = empty;
        }

        @Override
        public final T getResult() {
            return m_result;
        }

        @Override
        public void reset() {
        }

        @Override
        public final String toString() {
            return m_result != null ? m_result.toString() : null;
        }
    }

    public static class TextLoader extends AsbstractTextLoader<StringBuilder> {

        public TextLoader() {
            super(new StringBuilder());
        }

        @Override
        public void reset() {
            m_result.setLength(0);
        }

        @Override
        public boolean onLine(final String line) {
            m_result.append(line).append(EOL);
            return true;
        }
    }
}
