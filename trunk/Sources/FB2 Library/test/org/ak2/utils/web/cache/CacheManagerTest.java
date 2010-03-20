package org.ak2.utils.web.cache;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class CacheManagerTest {

    @Test
    public void listCacheContent() {
        for (CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }
    }

    @Test
    public void testJSON() throws JSONException {
        for (CachedContent content : CacheManager.getInstance()) {
            System.out.println(new JSONObject(content).toString(2));
        }
    }

    @Test
    public void testCacheFolder() {
        System.out.println("user.dir       =" + System.getProperty("user.dir"));
        System.out.println("user.home      =" + System.getProperty("user.home"));
        System.out.println("java.io.tmpdir =" + System.getProperty("java.io.tmpdir"));
        
    }
}
