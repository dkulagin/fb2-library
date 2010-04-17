package org.ak2.fb2.shelf.catalog;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.ak2.utils.StreamUtils;
import org.ak2.utils.jlog.JLog;
import org.ak2.utils.jlog.JLogLevel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShelfCatalogTest {

    private static final File CATALOG = new File("catalog.0.xml");

    @BeforeClass
    public static void initLog() {
        JLog.setConsoleLevel(JLogLevel.ERROR);
    }

    @Test
    public void testXML() {
        ShelfCatalog catalog = new ShelfCatalog(CATALOG);
        Assert.assertNotNull(catalog);
        System.out.println("Books: " + catalog.newStorage(catalog).getEntityCount());
    }

    @Test
    public void testJSON() throws Exception {
        StringBuilder text = StreamUtils.loadText(new FileReader(CATALOG), new StreamUtils.TextLoader());
        JSONObject jsonObject = XML.toJSONObject(text.toString());

        JSONObject root = jsonObject.getJSONObject("books");
        JSONObject location = root.getJSONObject("location");
        String locationBase = location.getString("base");
        JSONArray books = location.getJSONArray("book");
        
        List<BookInfo> list = new ArrayList<BookInfo>();
        
        for (int i = 0; i < books.length(); i++) {
            JSONObject book = books.getJSONObject(i);
            list.add(new BookInfo(locationBase, book));
        }
        System.out.println("Books: " + list.size());
    }

}
