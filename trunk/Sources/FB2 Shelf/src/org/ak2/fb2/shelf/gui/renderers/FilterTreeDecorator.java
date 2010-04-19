package org.ak2.fb2.shelf.gui.renderers;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.html.HTML.Tag;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.ak2.fb2.shelf.gui.models.tree.AuthorFilterNode;
import org.ak2.fb2.shelf.gui.models.tree.AuthorPackFilterNode;
import org.ak2.fb2.shelf.gui.models.tree.RootFilterNode;
import org.ak2.fb2.shelf.gui.models.tree.SequenceFilterNode;
import org.ak2.gui.controls.tree.TreeEx;
import org.ak2.gui.models.tree.AbstractTreeNode;
import org.ak2.gui.resources.ResourceManager;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.html.HtmlBuilder;
import org.ak2.utils.html.HtmlBuilder.StyleSheet;

public class FilterTreeDecorator extends DefaultTreeCellRenderer {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -7345265769608698842L;

    /**
     * Sets the value of the current tree cell.
     *
     * @param tree
     *            tree component
     * @param value
     *            value to show
     * @param sel
     *            If <code>true</code>, the cell will be drawn as if selected
     * @param expanded
     *            If <code>true</code>, the node is currently expanded
     * @param leaf
     *            if <code>true</code>, the node represets a leaf
     * @param row
     *            tree node row
     * @param hasFocus
     *            if <code>true</code>, the node currently has focus.
     * @return the <code>Component</code> that the renderer uses to draw the value
     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf,
            final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, "", sel, expanded, leaf, row, hasFocus);
        decorate(this, value);
        return this;
    }

    public static void decorate(TreeEx tree) {
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        tree.setCellRenderer(new FilterTreeDecorator());

        ((BasicTreeUI) tree.getUI()).setExpandedIcon(getExpandedIcon());
        ((BasicTreeUI) tree.getUI()).setCollapsedIcon(getCollapsedIcon());
    }

    /**
     *
     * @param label
     *            component JLabel
     * @param value
     *            any object
     */
    public static void decorate(final JLabel label, final Object value) {
        final String text = text(value);
        final String desc = desc(value);
        final Icon icon = icon(value);

        label.setText(text);

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.selector("html").attr("color", "black").attr("background", "FFFFC4");

        HtmlBuilder buf = new HtmlBuilder();
        buf.start(Tag.HTML).style(styleSheet).start(Tag.DIV).text(desc).text(":").end().start(Tag.DIV).text(text);
        label.setToolTipText(buf.finish());

        label.setIcon(icon);
        label.setDisabledIcon(icon);
    }

    /**
     * @param userObject
     *            {@link Object}
     * @return text
     */
    public static String text(final Object userObject) {
        if (userObject instanceof TreePath) {
            final TreePath path = (TreePath) userObject;
            final Object[] nodes = path.getPath();
            final StringBuilder builder = new StringBuilder();
            for (final Object node : nodes) {
                if (builder.length() > 0) {
                    builder.append(" :: ");
                }
                builder.append(text(node));
            }
            return builder.toString();
        }

        return LengthUtils.safeString(LengthUtils.toString(userObject), " ");
    }

    /**
     *
     * @param userObject
     *            any object
     * @return string
     */
    public static String desc(final Object userObject) {
        if (userObject instanceof String) {
            return (String) userObject;
        }
        if (userObject instanceof RootFilterNode) {
            return "The whole shelf";
        }
        if (userObject instanceof AuthorFilterNode) {
            return "Author";
        }
        if (userObject instanceof AuthorPackFilterNode) {
            return "Authors";
        }
        if (userObject instanceof SequenceFilterNode) {
            return "Book sequence";
        }
        if (userObject instanceof TreePath) {
            final TreePath path = (TreePath) userObject;
            final Object[] nodes = path.getPath();

            final HtmlBuilder builder = new HtmlBuilder();
            builder.start(Tag.HTML);
            for (final Object node : nodes) {
                final String desc = LengthUtils.safeString(desc(node), text(node));
                builder.start(Tag.DIV).text(desc).end();
            }
            return builder.finish();
        }
        return "";

    }

    /**
     *
     * @param userObject
     *            any object
     * @return icon
     */
    public static Icon icon(final Object userObject) {
        if (userObject instanceof String) {
            return ResourceManager.getInstance().getIcon((String) userObject);
        }
        if (userObject instanceof RootFilterNode) {
            return icon("ui/tree/all.png");
        }
        if (userObject instanceof AuthorPackFilterNode) {
            return icon("ui/tree/all.png");
        }
        if (userObject instanceof AuthorFilterNode) {
            return icon("ui/tree/author.png");
        }
        if (userObject instanceof SequenceFilterNode) {
            return icon("ui/tree/seq.png");
        }
        if (userObject instanceof TreePath) {
            final TreePath path = (TreePath) userObject;
            final Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent != null) {
                return icon(lastPathComponent);
            }
        }
        return null;
    }

    /**
     * Gets icon for expanded tree node / editor
     *
     * @return an instance of {@link Icon} interface
     */
    public static Icon getExpandedIcon() {
        return ResourceManager.getInstance().getIcon("ui/tree/collapse.png");
    }

    /**
     * Gets icon for collapsed tree node / editor
     *
     * @return an instance of {@link Icon} interface
     */
    public static Icon getCollapsedIcon() {
        return ResourceManager.getInstance().getIcon("ui/tree/expand.png");
    }

}
