package org.ak2.utils.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The Class PackageCreator. Creates output zip file package.
 *
 * @author Komarovskikh Andrei
 */
public class PackageCreator {

    /** The output zip file. */
    private ZipOutputStream outputZipFile;

    /**
     * The Constructor.
     *
     * @param outFilename
     *            the out filename
     * @throws IOException
     */
    public PackageCreator(final File outFile) throws IOException {
        outFile.createNewFile();
        FileOutputStream out = new FileOutputStream(outFile);
        outputZipFile = new ZipOutputStream(out);
    }

    /**
     * Adds content of the stream to package.
     *
     * @param inputFile
     *            the input file
     * @throws IOException
     *             the IO exception
     */
    public void addFileToPackage(final InputStream inStream, final String inputFile) throws IOException {
        // Create a buffer for reading the files
        final byte[] buf = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // Transfer bytes from the file to the ZIP file
        int len, length = 0;
        while ((len = inStream.read(buf)) > 0) {
            bos.write(buf, 0, len);
            length += len;
        }
        byte[] bytes = bos.toByteArray();
        addFileToPackage(bytes, inputFile);
    }

    /**
     * Adds content of the stream to package.
     *
     * @param inputFile
     *            the input file
     * @throws IOException
     *             the IO exception
     */
    public void addFileToPackage(final byte[] bytes, final String inputFile) throws IOException {
        final String entryName = toTranslit(inputFile);
        ZipEntry zipEntry = new ZipEntry(entryName);
        ZipOutputStream fakeZip = new ZipOutputStream(new ByteArrayOutputStream());
        fakeZip.putNextEntry(zipEntry);
        fakeZip.write(bytes);
        fakeZip.closeEntry();
        fakeZip.close();

        // Add ZIP entry to output stream.
        outputZipFile.putNextEntry(zipEntry);
        outputZipFile.write(bytes);
        // Complete the entry
        outputZipFile.closeEntry();
    }

    /**
     * Close.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        outputZipFile.close();
    }

    private static HashMap<String, String> map = new HashMap<String, String>();
    static {
        map.put("\u0430", "a");
        map.put("\u0431", "b");
        map.put("\u0432", "v");
        map.put("\u0433", "g");
        map.put("\u0434", "d");
        map.put("\u0435", "e");
        map.put("\u0451", "jo");
        map.put("\u0436", "zh");
        map.put("\u0437", "z");
        map.put("\u0438", "i");
        map.put("\u0439", "j");
        map.put("\u043a", "k");
        map.put("\u043b", "l");
        map.put("\u043c", "m");
        map.put("\u043d", "n");
        map.put("\u043e", "o");
        map.put("\u043f", "p");
        map.put("\u0440", "r");
        map.put("\u0441", "s");
        map.put("\u0442", "t");
        map.put("\u0443", "u");
        map.put("\u0444", "f");
        map.put("\u0445", "h");
        map.put("\u0446", "c");
        map.put("\u0447", "ch");
        map.put("\u0448", "sh");
        map.put("\u0449", "shch");
        map.put("\u044a", "");
        map.put("\u044b", "y");
        map.put("\u044c", "'");
        map.put("\u044d", "ye");
        map.put("\u044e", "ju");
        map.put("\u044f", "ja");

        map.put("\u00ab", "_");
        map.put("\u00bb", "_");

        map.put(" ", "_");
    }

    private String toTranslit(String inputFile) {
        inputFile = inputFile.toLowerCase();
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < inputFile.length(); i++) {
            char charAt = inputFile.charAt(i);
            String mapped = map.get(new String(new char[] { charAt }));
            if (mapped != null) {
                out.append(mapped);
            } else {
                out.append(charAt);
            }
        }
        return out.toString();
    }
}
