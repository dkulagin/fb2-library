package test.editors;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XmlUtils {

    public static final String EOL = "\n";

    /**
     * Constructor.
     */
    private XmlUtils() {
    }

    public static boolean isLastTextFragment(final Node node) {
        Node paragraph = null;
        for (Node parent = node.getParentNode(); parent != null && paragraph == null; parent = parent.getParentNode()) {
            if ("p".equals(parent.getNodeName())) {
                paragraph = parent;
            }
        }
        if (paragraph == null) {
            return false;
        }

        final Node lastChild = XmlUtils.getLastTextChild(paragraph);
        return lastChild == node;
    }

    public static Node getLastTextChild(final Node parent) {
        final NodeList childNodes = parent.getChildNodes();
        for (int i = childNodes.getLength() - 1; i >= 0; i++) {
            final Node item = childNodes.item(i);
            if (item instanceof Text) {
                return item;
            }
            final Node inner = getLastTextChild(item);
            if (inner != null) {
                return inner;
            }
        }
        return null;
    }

    public static void fixEmptyNode(final Node node) {
        if (node != null && node.getFirstChild() == null) {
            final Node parentNode = node.getParentNode();
            if (parentNode != null) {
                parentNode.removeChild(node);
                fixEmptyNode(parentNode);
            }
        }
    }

    public static Node getPNode(final Node node) {
        final Node parentNode = node.getParentNode();
        if (parentNode != null) {
            if ("p".equals(parentNode.getNodeName())) {
                return parentNode;
            } else {
                return getPNode(parentNode);
            }
        }
        return null;
    }

    public static Node getNextParagraph(final Node paragraph) {
        Node nextSibling = paragraph.getNextSibling();
        while (nextSibling != null && !"p".equals(nextSibling.getNodeName())) {
            nextSibling = nextSibling.getNextSibling();
        }
        return nextSibling;
    }

    public static String getParentNodeName(final Node node) {
        if (node == null) {
            return null;
        }
        final Node parentNode = node.getParentNode();
        if (parentNode != null) {
            return parentNode.getNodeName();
        }
        return null;
    }

    public static void moveNodes(final Node firstParagraph, final Node secondParagraph, final Node edgeNode) {
        final Node refChild = secondParagraph.getFirstChild();
        for (Node parent = edgeNode; parent != firstParagraph; parent = parent.getParentNode()) {
            for (Node movedNode = parent.getNextSibling(); movedNode != null; movedNode = parent.getNextSibling()) {
                movedNode.getParentNode().removeChild(movedNode);
                secondParagraph.insertBefore(movedNode, refChild);
            }
        }

    }
}
