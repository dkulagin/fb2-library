package org.ak2.fb2.shelf.gui.models.catalog;

import javax.swing.text.html.HTML;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.table.impl.BeanPropertyAdapter;
import org.ak2.gui.models.table.impl.TableModelEx;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.html.HtmlBuilder;
import org.ak2.utils.html.HtmlBuilder.StyleSheet;

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

    /**
     * Returns row tooltip for the given row index
     *
     * @param row
     *            row index
     * @return string
     * @see ITableModel#getTooltip(int)
     */
    @Override
    public String getTooltip(final int row) {
        BookInfo entity = getEntity(row);
        HtmlBuilder buf = new HtmlBuilder();

        final StyleSheet styleSheet = new StyleSheet();
        styleSheet.selector("html").attr("color", "black").attr("background", "FFFFC4");

        buf.start();
        buf.style(styleSheet);

        buf.start(HTML.Tag.DIV, "author").text(entity.getAuthor()).end();
        if (LengthUtils.isNotEmpty(entity.getSequence())) {
            buf.start(HTML.Tag.DIV, "seq").text(entity.getSequence() + " " + entity.getSequenceNo()).end();
        }
        buf.start(HTML.Tag.DIV, "title").text(entity.getBookName()).end();
        buf.start(HTML.Tag.DIV, "location").text(entity.getLocation()).end();
        buf.start(HTML.Tag.DIV, "location").text(entity.getContainer()).end();
        buf.start(HTML.Tag.DIV, "location").text(entity.getFile()).end();

        return buf.finish();
    }

}
