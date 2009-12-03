package org.ak2.utils.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexander Kasatkin
 */
public interface IXPathApi {

    /**
     * Use an XPath string to select a single node. XPath namespace prefixes are resolved from the context node, which
     * may not be what you want (see the next method).
     * 
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     * @throws XPathException thrown on errors
     */
    Node selectNode(Node contextNode, String str) throws XPathException;

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes are resolved from the contextNode.
     * 
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @return A NodeIterator, should never be null.
     * @throws XPathException thrown on errors
     */
    NodeList selectNodes(Node contextNode, String str) throws XPathException;

    /**
     * Factory class.
     */
    public static final class Factory {

        /**
         * @return new instance of the {@link IXPathApi} object
         */
        public static IXPathApi newInstance() {
            try {
                return new XPath15();
            } catch (final RuntimeException re) {
                return new XPath14();
            }
        }

    }
}
