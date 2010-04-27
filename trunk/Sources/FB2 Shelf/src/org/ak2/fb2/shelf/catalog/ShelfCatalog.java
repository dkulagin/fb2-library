package org.ak2.fb2.shelf.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ak2.gui.models.table.IFactory;
import org.ak2.gui.models.table.IStorage;
import org.ak2.gui.models.table.impl.ListStorage;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class ShelfCatalog implements IFactory<BookInfo, ShelfCatalog>, Iterable<BookInfo> {

    private static final JLogMessage MSG_LOAD_START = new JLogMessage(JLogLevel.DEBUG, "Loading catalog from {0} started...");

    private static final JLogMessage MSG_LOAD_ERROR = new JLogMessage(JLogLevel.ERROR, "Loading catalog from {0} failed: ");

    private static final JLogMessage MSG_LOAD_FINISH = new JLogMessage(JLogLevel.DEBUG, "Loading catalog from {0} finished.");

    private final File m_original;

    private final List<BookInfo> m_books = new LinkedList<BookInfo>();

    public ShelfCatalog(File xmlCatalog) {
        m_original = xmlCatalog;
        if (MSG_LOAD_START.isEnabled()) {
            MSG_LOAD_START.log(m_original.getName());
        }
        try {
            InputStreamReader r = new InputStreamReader(new FileInputStream(m_original), "UTF-8");
            JSONObject jsonObject = XML.toJSONObject(r);

            JSONObject root = jsonObject.getJSONObject("books");
            JSONObject location = root.getJSONObject("location");

            String locationBase = location.getString("base");
            locationBase = ShelfCatalogProvider.getLocationMapping(locationBase);

            JSONArray books = location.getJSONArray("book");

            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);
                m_books.add(new BookInfo(locationBase, book));
            }
        } catch (Exception ex) {
            MSG_LOAD_ERROR.log(ex, m_original.getName());
        } finally {

            Collections.sort(m_books);

            if (MSG_LOAD_FINISH.isEnabled()) {
                MSG_LOAD_FINISH.log(m_original.getName());
            }
        }
    }

    @Override
    public BookInfo newInstance() {
        return null;
    }

    @Override
    public IStorage<BookInfo> newStorage(ShelfCatalog catalog) {
        return new ListStorage<BookInfo>(catalog.m_books);
    }

    @Override
    public Iterator<BookInfo> iterator() {
        return m_books.iterator();
    }
}
