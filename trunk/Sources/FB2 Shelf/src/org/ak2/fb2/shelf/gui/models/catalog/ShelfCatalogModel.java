package org.ak2.fb2.shelf.gui.models.catalog;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.impl.BeanPropertyAdapter;
import org.ak2.gui.models.table.impl.TableModelEx;

public class ShelfCatalogModel extends TableModelEx<BookInfo, ShelfCatalog> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2331163205004452784L;

    private static final String[] COLUMNS = { "Author", "No", "Title" };

    private static final ITableColumnAdapter[] ADAPTERS = {
    /*  */
    new BeanPropertyAdapter(BookInfo.class, "Author"),
    /* */
    new BeanPropertyAdapter(BookInfo.class, "IntSequenceNo"), 
    /* */
    new BeanPropertyAdapter(BookInfo.class, "BookName"), 
    /* */
    };

    public ShelfCatalogModel(final ShelfCatalog catalog) {
        super(catalog, COLUMNS, ADAPTERS);
        this.setData(catalog);
    }
}
