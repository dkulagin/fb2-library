package org.ak2.fb2.shelf.catalog;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ak2.gui.models.table.IFactory;
import org.ak2.gui.models.table.IStorage;
import org.ak2.gui.models.table.impl.ListStorage;
import org.ak2.utils.XmlUtils;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.ak2.utils.threadlocal.ThreadLocalDocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ShelfCatalog implements IFactory<BookInfo, ShelfCatalog>, Iterable<BookInfo> {

    private static final JLogMessage MSG_LOAD_ERROR = new JLogMessage(JLogLevel.ERROR, "Loading catalog from {0} failed: ");

    private final File m_original;

    private final List<BookInfo> m_books = new LinkedList<BookInfo>();

    public ShelfCatalog(File xmlCatalog) {
        m_original = xmlCatalog;
        try {
            Document doc = ThreadLocalDocumentBuilder.parse(xmlCatalog);
            String location = null;
            for (Node locNode : XmlUtils.selectNodes(doc, "/books/location")) {
                location = XmlUtils.getString(locNode, "@base");
                for (Node bookNode : XmlUtils.selectNodes(locNode, "book")) {
                    m_books.add(new BookInfo(location, bookNode));
                }
            }
        } catch (Exception ex) {
            MSG_LOAD_ERROR.log(ex);
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
