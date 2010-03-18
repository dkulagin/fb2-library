package org.ak2.utils.web.cache;

import org.junit.Test;


public class CacheManagerTest {

    @Test
    public void listCacheContent() {
        for(CachedContent content : CacheManager.getInstance()) {
            System.out.println(content);
        }
    }
}
