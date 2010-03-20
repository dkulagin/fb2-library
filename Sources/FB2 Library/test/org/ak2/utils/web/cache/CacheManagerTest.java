package org.ak2.utils.web.cache;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


public class CacheManagerTest {

    @Test
    public void listCacheContent() {
        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }
    }
    
    @Test
    public void testJSON() throws JSONException {
        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(new JSONObject(content).toString(2));
        }
    }
}
