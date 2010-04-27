package org.ak2.utils.html;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.text.html.HTML;

import org.ak2.utils.LengthUtils;

/**
 * This class encapsulates HTML creation.
 */
public class HtmlBuilder
{
    private static final String CLOSE_TAG_BRACKET = ">";

    private static final String OPEN_TAG_BRACKET = "<";

    private static final String SPACE = " ";

    private final StringBuilder m_text = new StringBuilder();

    private final LinkedList<String> m_stack = new LinkedList<String>();

    private boolean m_tagOpened;

    /**
     * Constructor
     */
    public HtmlBuilder()
    {
    }

    /**
     * Finishes HMTL creation.
     *
     * @return HMTL string
     */
    public String finish()
    {
        while (!m_stack.isEmpty())
        {
            end();
        }

        return m_text.toString();
    }

    /**
     * Creates HTML tag.
     *
     * @param tag HTML tag
     * @return this
     */
    public HtmlBuilder tag(final HTML.Tag tag)
    {
        closeTag();
        m_text.append(OPEN_TAG_BRACKET).append(tag.toString()).append(CLOSE_TAG_BRACKET);
        return this;
    }

    /**
     * Starts HTML
     *
     * @return this
     */
    public HtmlBuilder start()
    {
        return start(HTML.Tag.HTML);
    }

    /**
     * Starts HTML tag
     *
     * @param tag HTML tag
     * @return this
     */
    public HtmlBuilder start(final HTML.Tag tag)
    {
        closeTag();
        String tagName = tag.toString();
        m_text.append(OPEN_TAG_BRACKET).append(tagName);
        m_tagOpened = true;
        m_stack.add(tagName);
        return this;
    }

    /**
     * Starts HTML tag.
     *
     * @param tag HTML tag
     * @param className the class name
     *
     * @return this
     */
    public HtmlBuilder start(final HTML.Tag tag, final String className)
    {
        return start(tag).attr(HTML.Attribute.CLASS, className);
    }

    /**
     * Creates tag attribute
     *
     * @param attr HTML attribute
     * @param value attribute value
     * @return this
     */
    public HtmlBuilder attr(final HTML.Attribute attr, final Object value)
    {
        if (m_tagOpened)
        {
            m_text.append(SPACE).append(attr.toString()).append("=");
            m_text.append("'").append(LengthUtils.toString(value)).append("'");
        }
        return this;
    }

    /**
     * Adds text into HTML.
     *
     * @param text text to add
     * @return this
     */
    public HtmlBuilder text(final Object text)
    {
        closeTag();
        String string = LengthUtils.toString(text);
        string = string.replaceAll(OPEN_TAG_BRACKET, "&lt;");
        string = string.replaceAll(CLOSE_TAG_BRACKET, "&gt;");
        m_text.append(string);
        return this;
    }

    /**
     * Adds space into HTML.
     *
     * @return this
     */
    public HtmlBuilder nbsp()
    {
        closeTag();
        m_text.append("&nbsp;");
        return this;
    }


    /**
     * Ends HTML tag.
     *
     * @return this
     */
    public HtmlBuilder end()
    {
        closeTag();
        if (!m_stack.isEmpty())
        {
            final String tagName = m_stack.removeLast();
            m_text.append(OPEN_TAG_BRACKET).append("/").append(tagName).append(CLOSE_TAG_BRACKET);
        }
        return this;
    }

    /**
     * Ends HTML tags in tag stack including the given one.
     *
     * @param tag name of HTML tag
     * @return this
     */
    public HtmlBuilder end(final HTML.Tag tag)
    {
        closeTag();
        String upTo = tag.toString();
        while (!m_stack.isEmpty())
        {
            final String tagName = m_stack.removeLast();
            m_text.append(OPEN_TAG_BRACKET).append("/").append(tagName).append(CLOSE_TAG_BRACKET);
            if (tagName.equalsIgnoreCase(upTo))
            {
                break;
            }
        }
        return this;
    }

    /**
     * Adds class attribute to a tag
     *
     * @param className class name
     * @return this
     */
    public HtmlBuilder tagClass(final String className)
    {
        return this.attr(HTML.Attribute.CLASS, className);
    }

    /**
     * Adds style sheet table
     *
     * @param styleSheet style sheet data
     * @return this
     */
    public HtmlBuilder style(final StyleSheet styleSheet)
    {
        return this.start(HTML.Tag.STYLE).text(styleSheet.toString()).end();
    }

    /**
     * Closes tag definition after inner attributes.
     */
    protected void closeTag()
    {
        if (m_tagOpened)
        {
            m_tagOpened = false;
            m_text.append(CLOSE_TAG_BRACKET);
        }
    }

    /**
     * This class encapsulates style sheet data
     */
    public static class StyleSheet
    {
        private final Map<String, Map<String, String>> m_data = new TreeMap<String, Map<String, String>>();

        private String m_selector;

        /**
         * Constructor
         */
        public StyleSheet()
        {
        }

        /**
         * Sets a current selector
         *
         * @param selector selector to set
         * @return this
         */
        public StyleSheet selector(final String selector)
        {
            m_selector = selector;
            return this;
        }

        /**
         * Adds style attribute to the current selector
         *
         * @param name attribute name
         * @param value attribute value
         * @return this
         */
        public StyleSheet attr(final String name, final String value)
        {
            if (LengthUtils.isNotEmpty(m_selector))
            {
                attr(m_selector, name, value);
            }
            return this;
        }

        /**
         * Adds style attribute to the given selector
         *
         * @param selector style selector
         * @param name attribute name
         * @param value attribute value
         * @return this
         */
        public StyleSheet attr(final String selector, final String name, final String value)
        {
            Map<String, String> map = m_data.get(selector);
            if (map == null)
            {
                map = new TreeMap<String, String>();
                m_data.put(selector, map);
            }
            map.put(name, value);
            return this;
        }

        /**
         * @return style sheet text
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            final StringBuilder buf = new StringBuilder();
            for(final Entry<String, Map<String, String>> selector : m_data.entrySet())
            {
                final Map<String, String> attributes = selector.getValue();
                if (!attributes.isEmpty())
                {
                    buf.append(selector.getKey());
                    buf.append(SPACE);
                    buf.append("{");

                    for(final Entry<String, String> attr : attributes.entrySet())
                    {
                        buf.append(attr.getKey());
                        buf.append(":");
                        buf.append(attr.getValue());
                        buf.append(";");
                    }
                    buf.append("}");
                    buf.append(SPACE);
                }
            }

            return buf.toString();
        }

    }

}