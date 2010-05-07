package org.ak2.fb2.shelf.gui.components;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import org.ak2.gui.actions.ActionEx;
import org.ak2.gui.actions.ActionGroup;
import org.ak2.gui.controls.panels.FilterField;
import org.ak2.gui.controls.panels.TitledTreePanel;

public class FilterTreePanel extends TitledTreePanel {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4882241151716226890L;

    public static final String ACT_AUTHORS = "authors";

    public static final String ACT_SEQUENCES = "sequences";

    public FilterTreePanel() {
        super(new FilterField());
        this.setTitle("Book shelf");
        this.setParallelFilter(true);
    }

    @Override
    protected JPopupMenu createPopupMenu() {
        final ActionEx showAuthors = getController().getAction(ACT_AUTHORS);
        final ActionEx showSequences = getController().getAction(ACT_SEQUENCES);

        if (showAuthors != null && showSequences != null) {
            new ActionGroup(showAuthors, showSequences);

            final JPopupMenu menu = new JPopupMenu();
            menu.setInvoker(this);

            final JCheckBoxMenuItem item1 = showAuthors.createCheckMenuItem();
            final JCheckBoxMenuItem item2 = showSequences.createCheckMenuItem();

            showAuthors.setSourceSelected(true);

            menu.add(item1);
            menu.add(item2);

            return menu;
        }

        return null;
    }

}
