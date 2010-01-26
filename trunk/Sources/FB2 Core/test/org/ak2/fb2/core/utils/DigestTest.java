package org.ak2.fb2.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.ak2.fb2.core.utils.Digest;
import org.junit.Assert;
import org.junit.Test;

public class DigestTest {

    public static final String TEST = "This is ������";

    @Test
    public void testDigest() throws NoSuchAlgorithmException, IOException {
        final String hash1 = Digest.digest(TEST);
        final String hash2 = Digest.digest(TEST);
        final String hash3 = Digest.digest(new ByteArrayInputStream(TEST.getBytes()));
        final String hash4 = Digest.digest(new ByteArrayInputStream(TEST.getBytes()));
        Assert.assertEquals("Hashes [1,2] are different", hash1, hash2);
        Assert.assertEquals("Hashes [1,3] are different", hash1, hash3);
        Assert.assertEquals("Hashes [3,4] are different", hash3, hash4);
    }

    @Test
    public void testMatches() throws NoSuchAlgorithmException, IOException {
        final String hash = Digest.digest(TEST);
        final ByteArrayInputStream stream = new ByteArrayInputStream(TEST.getBytes());
        final boolean matches = Digest.matches(stream, hash);
        Assert.assertTrue("Hashes are different", matches);
    }

}
