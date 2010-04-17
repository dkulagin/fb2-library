package org.ak2.utils.xml;

import junit.framework.Assert;

import org.junit.Test;


public class XmlBuilderTest {

    @Test
    public void testEscaping() {
        Assert.assertEquals("QWE", XmlBuilder.escape("QWE"));
        Assert.assertEquals("&#60;QWE abs=&#34;rwerwer&#34;&#47;&#62;", XmlBuilder.escape("<QWE abs=\"rwerwer\"/>"));
    }
}