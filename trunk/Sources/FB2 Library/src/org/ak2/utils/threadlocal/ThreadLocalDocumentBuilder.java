package org.ak2.utils.threadlocal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ThreadLocalDocumentBuilder {

    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    /**
     * Thread buffers.
     */
    private static final ThreadLocal<DocumentBuilder> m_builders = new ThreadLocal<DocumentBuilder>() {
        /**
         * {@inheritDoc}
         *
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected DocumentBuilder initialValue() {
            try {
                return FACTORY.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
            }
            return null;
        }
    };

    private ThreadLocalDocumentBuilder() {
    }

    public static DOMImplementation getDOMImplementation() {
        return m_builders.get().getDOMImplementation();
    }

    public static Schema getSchema() {
        return m_builders.get().getSchema();
    }

    public static boolean isNamespaceAware() {
        return m_builders.get().isNamespaceAware();
    }

    public static boolean isValidating() {
        return m_builders.get().isValidating();
    }

    public static boolean isXIncludeAware() {
        return m_builders.get().isXIncludeAware();
    }

    public static Document newDocument() {
        return m_builders.get().newDocument();
    }

    public static Document parse(File f) throws SAXException, IOException {
        return m_builders.get().parse(f);
    }

    public static Document parse(InputSource is) throws SAXException, IOException {
        return m_builders.get().parse(is);
    }

    public static Document parse(InputStream is, String systemId) throws SAXException, IOException {
        return m_builders.get().parse(is, systemId);
    }

    public static Document parse(InputStream is) throws SAXException, IOException {
        return m_builders.get().parse(is);
    }

    public static Document parse(String uri) throws SAXException, IOException {
        return m_builders.get().parse(uri);
    }

    public static void reset() {
        m_builders.get().reset();
    }

    public static void setEntityResolver(EntityResolver er) {
        m_builders.get().setEntityResolver(er);
    }

    public static void setErrorHandler(ErrorHandler eh) {
        m_builders.get().setErrorHandler(eh);
    }

}
