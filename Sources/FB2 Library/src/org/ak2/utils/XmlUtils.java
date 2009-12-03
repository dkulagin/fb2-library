package org.ak2.utils;

import java.util.Iterator;

import org.ak2.utils.xpath.IXPathApi;
import org.ak2.utils.xpath.XPathException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexander Kasatkin
 */
public final class XmlUtils {

    /**
     * XPath API instance.
     */
    private static final IXPathApi XPATH = IXPathApi.Factory.newInstance();

    /**
     * Fake constructor.
     */
    private XmlUtils() {
    }

    /**
     * Searches for a content of a node by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @return string
     */
    public static String getString(final Node root, final String xpath) {
        Node node = null;
        try {
            node = XPATH.selectNode(root, xpath);
        } catch (final XPathException ex) {
            // NOP
        }
        return node != null ? node.getTextContent() : null;
    }

    /**
     * Searches for a content of a node by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @param defaultValue default value used if no appropriate content found
     * @return string
     */
    public static String getString(final Node root, final String xpath, final String defaultValue) {
        return LengthUtils.safeString(getString(root, xpath), defaultValue);
    }

    /**
     * Searches for a content of a node by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @param defaultValue default value used if no appropriate content found
     * @return boolean
     */
    public static boolean getBoolean(final Node root, final String xpath, final boolean defaultValue) {
        final String value = getString(root, xpath);
        if (LengthUtils.isNotEmpty(value)) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    /**
     * Searches for a content of a node by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @param defaultValue default value used if no appropriate content found
     * @return int
     */
    public static int getInteger(final Node root, final String xpath, final int defaultValue) {
        try {
            final String value = getString(root, xpath);
            if (LengthUtils.isNotEmpty(value)) {
                return Integer.parseInt(value);
            }
        } catch (final Exception ex) {
            // NOP
        }
        return defaultValue;
    }

    /**
     * Searches for a single node by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @return an instance of the {@link Node} object
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> T selectNode(final Node root, final String xpath) {
        try {
            return (T) XPATH.selectNode(root, xpath);
        } catch (final XPathException ex) {
            // NOP
        }
        return null;
    }

    /**
     * Searches for nodes by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @return an iterable collection of the {@link Node} objects
     */
    public static Iterable<Node> selectNodes(final Node root, final String xpath) {
        NodeList selectNodes = null;
        try {
            selectNodes = XPATH.selectNodes(root, xpath);
        } catch (final XPathException ex) {
            // NOP
        }
        return new NodeListWrapper(selectNodes);
    }

    /**
     * Searches for content onodes by the given XPath expression.
     * 
     * @param root root XML element
     * @param xpath XPath expression
     * @return an iterable collection of selected node contents
     */
    public static Iterable<String> selectStrings(final Node root, final String xpath) {
        NodeList selectNodes = null;
        try {
            selectNodes = XPATH.selectNodes(root, xpath);
        } catch (final XPathException ex) {
            // NOP
        }
        return new StringListWrapper(selectNodes);
    }

    /**
     * This class wraps the {@link NodeList} object by iterable collection interface.
     */
    private static class NodeListWrapper implements Iterable<Node>, Iterator<Node> {

        /**
         * List containing found nodes.
         */
        private final NodeList m_nodes;

        /**
         * Iterator index.
         */
        private int m_index;

        /**
         * Constructor.
         * 
         * @param nodes found nodes
         */
        NodeListWrapper(final NodeList nodes) {
            m_nodes = nodes;
        }

        /**
         * Returns an iterator over a set of elements of type T.
         * 
         * @return an Iterator.
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<Node> iterator() {
            m_index = 0;
            return this;
        }

        /**
         * Returns <tt>true</tt> if the iteration has more elements.
         * 
         * @return <tt>true</tt> if the iterator has more elements.
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return m_nodes != null && m_index >= 0 && m_index < m_nodes.getLength();
        }

        /**
         * Returns the next element in the iteration.
         * 
         * @return the next element in the iteration.
         * @see java.util.Iterator#next()
         */
        public Node next() {
            return hasNext() ? m_nodes.item(m_index++) : null;
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
        }

    }

    /**
     * This class wraps the {@link NodeList} object by iterable collection interface.
     */
    private static class StringListWrapper implements Iterable<String>, Iterator<String> {

        /**
         * List containing found nodes.
         */
        private final NodeList m_nodes;

        /**
         * Iterator index.
         */
        private int m_index;

        /**
         * Constructor.
         * 
         * @param nodes found nodes
         */
        StringListWrapper(final NodeList nodes) {
            m_nodes = nodes;
        }

        /**
         * Returns an iterator over a set of elements of type T.
         * 
         * @return an Iterator.
         * @see java.lang.Iterable#iterator()
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<String> iterator() {
            m_index = 0;
            return this;
        }

        /**
         * Returns <tt>true</tt> if the iteration has more elements.
         * 
         * @return <tt>true</tt> if the iterator has more elements.
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return m_nodes != null && m_index >= 0 && m_index < m_nodes.getLength();
        }

        /**
         * Returns the next element in the iteration.
         * 
         * @return the next element in the iteration.
         * @see java.util.Iterator#next()
         */
        public String next() {
            return hasNext() ? m_nodes.item(m_index++).getTextContent() : null;
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
        }

    }

}
