package org.ak2.utils.xpath;

import java.lang.reflect.Method;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexander Kasatkin
 */
public class XPath14 implements IXPathApi {

    /**
     * Implemented the {@link #selectNode(Node, String)} method.
     */
    private Method m_selectSingle;

    /**
     * Implemented the {@link #selectNodes(Node, String)} method.
     */
    private Method m_selectNodes;

    /**
     * Constructor.
     */
    public XPath14() {
        try {
            final Class<?> cl = Class.forName("org.apache.xpath.XPathAPI");
            m_selectSingle = cl.getMethod("selectSingleNode", Node.class, String.class);
            m_selectNodes = cl.getMethod("selectNodeList", Node.class, String.class);
        } catch (final Exception e) {
            // NOP
        }
    }

    /**
     * Use an XPath string to select a single node. XPath namespace prefixes are resolved from the context node, which
     * may not be what you want (see the next method).
     * 
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     * @throws XPathException thrown on errors
     * @see org.ak2.utils.xpath.IXPathApi#selectNode(org.w3c.dom.Node, java.lang.String)
     */
    public Node selectNode(final Node contextNode, final String str) throws XPathException {
        try {
            return (Node) m_selectSingle.invoke(null, contextNode, str);
        } catch (final Exception ex) {
            throw new XPathException(ex);
        }
    }

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes are resolved from the contextNode.
     * 
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @return A NodeIterator, should never be null.
     * @throws XPathException thrown on errors
     * @see org.ak2.utils.xpath.IXPathApi#selectNodes(org.w3c.dom.Node, java.lang.String)
     */
    public NodeList selectNodes(final Node contextNode, final String str) throws XPathException {
        try {
            return (NodeList) m_selectNodes.invoke(null, contextNode, str);
        } catch (final Exception ex) {
            throw new XPathException(ex);
        }
    }
}
