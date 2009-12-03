package org.ak2.utils.xpath;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexander Kasatkin
 */
public class XPath15 implements IXPathApi {

    /**
     * XPath factory.
     */
    private final XPathFactory m_factory;

    /**
     * Constructor.
     */
    public XPath15() {
        m_factory = XPathFactory.newInstance();
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
            final XPath expr = m_factory.newXPath();
            return (Node) expr.evaluate(str, contextNode, XPathConstants.NODE);
        } catch (final XPathExpressionException ex) {
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
            final XPath expr = m_factory.newXPath();
            return (NodeList) expr.evaluate(str, contextNode, XPathConstants.NODESET);
        } catch (final XPathExpressionException ex) {
            throw new XPathException(ex);
        }
    }
}
