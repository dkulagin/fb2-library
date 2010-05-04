package org.ak2.gui.controls.panels;

import org.ak2.gui.controls.table.TableEx;

public class TitledTablePanel extends TitledComponentPanel<TableEx> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -1433915879610251615L;

    /**
     * Constructor
     *
     * @param parent
     *            parent controller
     */
    public TitledTablePanel() {
        super();
    }

    /**
     * Constructor
     *
     * @param parent
     *            parent controller
     * @param filterField
     *            filter field
     */
    public TitledTablePanel(final FilterField filterField) {
        super(filterField);
    }

    /**
     * @return an instance of the {@link TableEx} object
     * @see TitledComponentPanel#createInner()
     */
    @Override
    protected TableEx createInner() {
        return new TableEx();
    }

}
