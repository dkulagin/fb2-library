package org.ak2.fb2.shelf.catalog;

import org.ak2.fb2.library.book.FictionBookInfo;
import org.ak2.utils.XmlUtils;
import org.w3c.dom.Node;

public class BookInfo extends FictionBookInfo {

    private final String m_location;
    private final String m_container;
    private final String m_file;

    public BookInfo(String location, Node root) throws Exception {
        super(root);
        m_location = location;
        m_container = XmlUtils.getString(root, "@container");
        m_file = XmlUtils.getString(root, "@file");
    }

    public String getLocation() {
        return m_location;
    }

    public String getContainer() {
        return m_container;
    }

    public String getFile() {
        return m_file;
    }

}
