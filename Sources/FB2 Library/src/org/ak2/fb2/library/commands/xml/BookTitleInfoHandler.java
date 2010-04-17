package org.ak2.fb2.library.commands.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.files.IFile;
import org.ak2.utils.xml.XmlBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Alexander Kasatkin
 */
final class BookTitleInfoHandler extends DefaultHandler {

    private final static Set<String> EXCLUDED = new HashSet<String>(Arrays.asList("annotation", "coverpage", "id", "date", "lang"));

    private final XmlBuilder m_buffer = new XmlBuilder(false);

    private boolean m_inTitleInfo = false;

    private String m_excluded = null;

    private SAXParser m_parser;

    public BookTitleInfoHandler() {
    }

    public String parse(final IFile file) throws ParserConfigurationException, SAXException, IOException {
        if (m_parser == null) {
            m_parser = SAXParserFactory.newInstance().newSAXParser();
        }

        this.m_buffer.reset();
        this.m_inTitleInfo = false;
        this.m_excluded = null;

        final InputStream in = file.open();
        try {
            m_parser.parse(in, this);
        } catch (final StopParseException ex) {
        } finally {
            in.close();
        }
        return this.m_buffer.finish();
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        final String elemName = LengthUtils.safeString(localName, qName);
        if (!m_inTitleInfo && "title-info".equals(elemName)) {
            m_inTitleInfo = true;
        }
        if (m_inTitleInfo && m_excluded == null) {
            if (shouldExclude(elemName)) {
                m_excluded = elemName;
            } else {
                m_buffer.start(elemName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    final String name = LengthUtils.safeString(attributes.getLocalName(i), attributes.getQName(i));
                    final String value = attributes.getValue(i);
                    m_buffer.attr(name, value);
                }
            }
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (m_inTitleInfo && m_excluded == null) {
            String text = new String(ch, start, length);
            m_buffer.text(text.trim());
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (m_inTitleInfo) {
            final String elemName = LengthUtils.safeString(localName, qName);
            if (LengthUtils.equals(m_excluded, elemName)) {
                m_excluded = null;
            } else if (m_excluded == null) {
                m_buffer.end(elemName);
                if ("title-info".equals(elemName)) {
                    m_inTitleInfo = false;
                    throw new StopParseException();
                }
            }
        }
    }

    private boolean shouldExclude(final String elemName) {
        return EXCLUDED.contains(elemName);
    }
}