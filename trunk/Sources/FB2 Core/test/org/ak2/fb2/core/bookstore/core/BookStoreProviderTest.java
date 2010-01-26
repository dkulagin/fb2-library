/**
 *
 */
package org.ak2.fb2.core.bookstore.core;

import org.ak2.fb2.core.bookstore.Factory;
import org.ak2.fb2.core.bookstore.IBookStore;
import org.ak2.fb2.core.bookstore.IBookStoreProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Kasatkin
 *
 */
public class BookStoreProviderTest extends AbstractTest {

    @Test
    public void testCreate() {
        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store);
        Assert.assertTrue(store.exists());
    }

    @Test
    public void testOpen() {
        final IBookStoreProvider provider = Factory.getProvider();
        Assert.assertNotNull(provider);

        final IBookStore store1 = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store1);
        Assert.assertTrue(store1.exists());

        final IBookStore store2 = provider.openBookStore(s_storeFolder);
        Assert.assertNotNull(store2);
        Assert.assertTrue(store2.exists());
    }
}
