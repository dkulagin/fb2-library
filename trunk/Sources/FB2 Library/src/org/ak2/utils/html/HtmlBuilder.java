package org.ak2.utils.html;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ak2.utils.LengthUtils;

/**
 * This class encapsulates HTML creation.
 */
public class HtmlBuilder
{
    private StringBuilder m_text = new StringBuilder();

    private LinkedList<String> m_stack = new LinkedList<String>();

    private boolean m_tagOpened = false;

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
     * @param tagName tag name
     * @return this
     */
    public HtmlBuilder tag(final String tagName)
    {
        closeTag();
        m_text.append("<").append(tagName).append(">");
        return this;
    }

    /**
     * Starts HTML tag
     *
     * @param tagName tag name
     * @return this
     */
    public HtmlBuilder start(final String tagName)
    {
        closeTag();
        m_text.append("<").append(tagName);
        m_tagOpened = true;
        m_stack.add(tagName);
        return this;
    }

    /**
     * Starts HTML tag.
     *
     * @param tagName the tag name
     * @param className the class name
     *
     * @return this
     */
    public HtmlBuilder start(final String tagName, final String className)
    {
        return start(tagName).attr("class", className);
    }

    /**
     * Creates tag attribute
     *
     * @param name attribute name
     * @param value attribute value
     * @return this
     */
    public HtmlBuilder attr(final String name, final Object value)
    {
        if (m_tagOpened)
        {
            m_text.append(" ").append(name).append("=").append("'").append(LengthUtils.toString(value)).append("'");
        }
        return this;
    }

    /**
     * Adds text info HTML.
     *
     * @param text text to add
     * @return this
     */
    public HtmlBuilder text(final Object text)
    {
        closeTag();
        String string = LengthUtils.toString(text);
        string = string.replaceAll("<", "&lt;");
        string = string.replaceAll(">", "&gt;");
        m_text.append(string);
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
            String tagName = m_stack.removeLast();
            m_text.append("</").append(tagName).append(">");
        }
        return this;
    }

    /**
     * Ends HTML tags in tag stack including the given one.
     *
     * @param upTo name of HTML tag
     * @return this
     */
    public HtmlBuilder end(final String upTo)
    {
        closeTag();
        while (!m_stack.isEmpty())
        {
            String tagName = m_stack.removeLast();
            m_text.append("</").append(tagName).append(">");
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
        return this.attr("class", className);
    }

    /**
     * Adds style sheet table
     *
     * @param styleSheet style sheet data
     * @return this
     */
    public HtmlBuilder style(final StyleSheet styleSheet)
    {
        return this.start("style").text(styleSheet.toString()).end();
    }

    /**
     * Closes tag definition after inner attributes.
     */
    protected void closeTag()
    {
        if (m_tagOpened)
        {
            m_tagOpened = false;
            m_text.append(">");
        }
    }

    /**
     * This class encapsulates style sheet data
     */
    public static class StyleSheet
    {
        private Map<String, Map<String, String>> m_data = new TreeMap<String, Map<String, String>>();

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
        public String toString()
        {
            StringBuilder buf = new StringBuilder();
            for(Entry<String, Map<String, String>> selector : m_data.entrySet())
            {
                final Map<String, String> attributes = selector.getValue();
                if (!attributes.isEmpty())
                {
                    buf.append(selector.getKey());
                    buf.append(" ");
                    buf.append("{");

                    for(Entry<String, String> attr : attributes.entrySet())
                    {
                        buf.append(attr.getKey());
                        buf.append(":");
                        buf.append(attr.getValue());
                        buf.append(";");
                    }
                    buf.append("}");
                    buf.append(" ");
                }
            }

            return buf.toString();
        }

    }

}