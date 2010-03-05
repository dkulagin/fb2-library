package test.editors.document.v2.tree;

import java.util.LinkedList;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.tree.IFb2NodeVisitor.Result;

public class Fb2TreeWalker {

    public static void visit(final IFb2Node root, final IFb2NodeVisitor visitor) {
        final LinkedList<IFb2Node> nodes = new LinkedList<IFb2Node>();
        nodes.add(root);

        try {
            for (IFb2Node node = getFirst(nodes); node != null; node = getFirst(nodes)) {
                switch (visitor.handle(node)) {
                case Stop:
                    return;
                case ProcessOnlyChildren:
                    nodes.clear();
                case Continue:
                    if (node instanceof IFb2CompositeNode) {
                        nodes.addAll(0, ((IFb2CompositeNode) node).getChildren());
                    }
                    break;
                case NextSibling:
                    break;
                }
            }
        } catch (final Throwable th) {
            th.printStackTrace();
        } finally {
            nodes.clear();
        }
    }

    public static void visit(final IFb2Node root, final int offset, final IFb2NodeVisitor visitor) {
        visit(root, new OffsetFilter(offset, visitor));
    }

    public static void visit(final IFb2Node root, final int offset, final int length, final IFb2NodeVisitor visitor) {
        visit(root, new IntervalFilter(offset, length, visitor));
    }

    static Result min(final Result result1, final Result result2) {
        if (result1.ordinal() <= result2.ordinal()) {
            return result1;
        }
        return result2;
    }

    static IFb2Node getFirst(final LinkedList<IFb2Node> nodes) {
        return nodes.isEmpty() ? null : nodes.removeFirst();
    }

}
