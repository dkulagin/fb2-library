package org.ak2.utils.web;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.ak2.utils.web.http.HttpContent;
import org.junit.Test;

public class WebContentTest {

    @Test
    public void test() throws IOException {
        URL url = new URL("http://lib.rus.ec/a/3150");
        Assert.assertNotNull(url);
        
        IWebContent wc = new HttpContent(url);
        Assert.assertNotNull(wc);
    }
    
    
}
