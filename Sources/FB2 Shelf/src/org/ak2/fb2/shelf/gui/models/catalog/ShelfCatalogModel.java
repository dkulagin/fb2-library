package org.ak2.fb2.shelf.gui.models.catalog;

import java.util.List;

import javax.swing.text.html.HTML;

import org.ak2.fb2.shelf.catalog.BookInfo;
import org.ak2.fb2.shelf.catalog.FileInfo;
import org.ak2.fb2.shelf.catalog.ShelfCatalog;
import org.ak2.gui.models.table.IFactory;
import org.ak2.gui.models.table.IStorage;
import org.ak2.gui.models.table.ITableColumnAdapter;
import org.ak2.gui.models.table.ITableModel;
import org.ak2.gui.models.table.impl.BeanPropertyAdapter;
import org.ak2.gui.models.table.impl.ListStorage;
import org.ak2.gui.models.table.impl.TableModelEx;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.html.HtmlBuilder;
import org.ak2.utils.html.HtmlBuilder.StyleSheet;

public class ShelfCatalogModel extends TableModelEx<BookInfo, List<BookInfo>> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2331163205004452784L;

    public static final String[] COLUMNS = { "Author", "No", "Title" };

    public static final ITableColumnAdapter[] ADAPTERS = {
    /*  */
    new BeanPropertyAdapter(BookInfo.class, "Author"),
    /* */
    new BeanPropertyAdapter(BookInfo.class, "IntSequenceNo"),
    /* */
    new BeanPropertyAdapter(BookInfo.class, "BookName"),
    /* */
    };

    private static final StyleSheet TOOLTIP_CSS = createTooltipCss();

    public ShelfCatalogModel(final ShelfCatalog catalog) {
        super(new Factory(), COLUMNS, ADAPTERS);
        this.setData(catalog.getBooks());
    }

    public ShelfCatalogModel(final List<BookInfo> books) {
        super(new Factory(), COLUMNS, ADAPTERS);
        this.setData(books);
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
        final BookInfo entity = getEntity(row);
        final HtmlBuilder buf = new HtmlBuilder();

        buf.start();
        buf.style(TOOLTIP_CSS);

        buf.start(HTML.Tag.TABLE).attr(HTML.Attribute.WIDTH, "100%");

        buf.start(HTML.Tag.TR);
        buf.start(HTML.Tag.TD, "label").text("Book").text(":").end();
        buf.start(HTML.Tag.TD, "title").text(entity.getBookName()).end();
        buf.end(HTML.Tag.TR);

        if (LengthUtils.isNotEmpty(entity.getSequence())) {
            buf.start(HTML.Tag.TR);
            buf.start(HTML.Tag.TD, "label").text("Sequence").text(":").end();
            buf.start(HTML.Tag.TD, "seq").text(entity.getSequence() + " " + entity.getSequenceNo()).end();
            buf.end(HTML.Tag.TR);
        }
        buf.start(HTML.Tag.TR);
        buf.start(HTML.Tag.TD, "label").text("Author").text(":").end();
        buf.start(HTML.Tag.TD, "author").text(entity.getAuthor()).end();
        buf.end(HTML.Tag.TR);

        FileInfo fileInfo = entity.getFileInfo();
        boolean exists = fileInfo.getLocation().exists();

        buf.start(HTML.Tag.TR);
        buf.start(HTML.Tag.TD, "label").text("Location").text(":").end();
        buf.start(HTML.Tag.TD, exists ? "location" : "err_location").text(fileInfo.getLocationPath()).end();
        buf.end(HTML.Tag.TR);

        exists = exists && fileInfo.getContainer().exists();

        buf.start(HTML.Tag.TR);
        buf.start(HTML.Tag.TD, "label").nbsp().end();
        buf.start(HTML.Tag.TD, exists ? "location" : "err_location").text(fileInfo.getContainerPath()).end();
        buf.end(HTML.Tag.TR);

        exists = exists && fileInfo.getBook().exists();

        buf.start(HTML.Tag.TR);
        buf.start(HTML.Tag.TD, "label").nbsp().end();
        buf.start(HTML.Tag.TD, exists ? "location" : "err_location").text(fileInfo.getBookPath()).end();
        buf.end(HTML.Tag.TR);

        return buf.finish();
    }

    private static StyleSheet createTooltipCss() {
        final StyleSheet styleSheet = new StyleSheet();
        styleSheet.selector("html").attr("color", "black").attr("background", "FFFFC4");
        styleSheet.selector(".author").attr("font-weight", "bold");
        styleSheet.selector(".seq").attr("font-weight", "bold");
        styleSheet.selector(".title").attr("font-weight", "bold");
        styleSheet.selector(".location").attr("font-style", "italic");
        styleSheet.selector(".label").attr("text-align", "right");
        styleSheet.selector(".err_location").attr("color", "red").attr("font-style", "italic");
        return styleSheet;
    }

    private static class Factory implements IFactory<BookInfo, List<BookInfo>> {
        @Override
        public BookInfo newInstance() {
            return null;
        }

        @Override
        public IStorage<BookInfo> newStorage(List<BookInfo> container) {
            return new ListStorage<BookInfo>(container);
        }
    }

}
