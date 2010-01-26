package org.ak2.fb2.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Digest {

    /** SHA-1: The Secure Hash Algorithm, as defined in Secure Hash Standard, NIST FIPS 180-1. */
    private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-1";

    private final DigestInputStream fieldStream;

    private final MessageDigest fieldDigest;

    private String fieldResult;

    /**
     * Constructor
     *
     * @throws NoSuchAlgorithmException
     */
    public Digest(final InputStream stream) throws NoSuchAlgorithmException {
        fieldDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        fieldStream = new DigestInputStream(stream, fieldDigest);
        fieldDigest.reset();
    }

    public InputStream getInputStream() {
        return fieldStream;
    }

    public String getDigest() {
        if (fieldResult == null) {
            fieldResult = encode(fieldDigest.digest());
        }
        return fieldResult;
    }

    /**
     * Create an encoded hash for the given plain text.
     *
     * @param string Plain text
     * @return BASE64 encoded hash
     * @throws NoSuchAlgorithmException
     */
    public static String digest(final String string) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        md.reset();
        md.update(string.getBytes());
        return encode(md.digest());
    }

    /**
     * Create an encoded hash for the given input stream.
     *
     * @param stream input stream
     * @return BASE64 encoded hash
     * @throws NoSuchAlgorithmException
     */
    public static String digest(final InputStream stream) throws NoSuchAlgorithmException, IOException {
        final MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        md.reset();
        for (int value = stream.read(); value != -1; value = stream.read()) {
            md.update((byte) value);
        }
        stream.close();
        return encode(md.digest());
    }

    /**
     * Test if a plain text matches an encoded hash.
     *
     * @param string Plain text
     * @param hash BASE64 encoded hash
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static boolean matches(final String string, final String hash) throws NoSuchAlgorithmException {
        final String newHash = digest(string);
        return newHash.equals(hash);
    }

    /**
     * Test if a plain text matches an encoded hash.
     *
     * @param stream input stream
     * @param hash BASE64 encoded hash
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static boolean matches(final InputStream stream, final String hash) throws NoSuchAlgorithmException,
            IOException {
        final String newHash = digest(stream);
        return newHash.equals(hash);
    }

    private static String encode(final byte[] bytes) {
        return Base64.encodeBytes(bytes).replace('/', '-');
    }
}
