package org.ak2.utils.xml;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.threadlocal.ThreadLocalBuffer;

/**
 * @author Alexander Kasatkin
 */
public class XmlBuilder {

    /**
     * Escaped symbols
     */
    private static final Pattern ESCAPED = Pattern.compile("[\\&\\<\\\"\\>\\/]");

    /**
    *
    */
    private static final String IDENT = "  ";

    /**
     * Pretty printing flag.
     */
    boolean prettyPrint = true;

    /**
     * Internal buffer.
     */
    private static final ThreadLocalBuffer buf = new ThreadLocalBuffer(8192);

    /**
     * Internal buffer.
     */
    private static final ThreadLocalBuffer temp = new ThreadLocalBuffer(1024);

    /**
     * Tags stack.
     */
    private final Stack<String> stack = new Stack<String>();

    /**
     * Attributes of the opened top tag.
     */
    private final Map<String, String> attrs = new LinkedHashMap<String, String>();

    /**
     * Is buffer still in tag declaration.
     */
    private boolean inDecl = false;

    /**
     * Is buffer still in opened tag.
     */
    private boolean ended = false;

    /**
     * XML prolog flag.
     */
    private final boolean writeProlog;

    /**
     * Xml encoding.
     */
    private final String encoding;

    /**
     * Constructor.
     */
    public XmlBuilder() {
        this("UTF-8", true);
    }

    /**
     * Constructor.
     * 
     * @param encoding
     *            XML encoding
     * @param prettyPrint
     *            the pretty print flag
     */
    public XmlBuilder(final String encoding, final boolean prettyPrint) {
        this.encoding = encoding;
        this.prettyPrint = prettyPrint;
        this.writeProlog = true;
        reset();
    }

    /**
     * Constructor. No XML prolog written.
     * 
     * @param prettyPrint
     *            the pretty print flag
     */
    public XmlBuilder(final boolean prettyPrint) {
        this.encoding = null;
        this.prettyPrint = prettyPrint;
        this.writeProlog = false;
        reset();
    }

    /**
     * Resets builder.
     */
    public void reset() {
        buf.setLength(0);
        if (writeProlog) {
            buf.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\"?>");
        }
    }

    /**
     * Starts new tag.
     * 
     * @param name
     *            tag name
     * @return self
     */
    public XmlBuilder start(final String name) {
        finishDecl();
        stack.add(name);
        attrs.clear();
        inDecl = true;
        ended = false;
        return this;
    }

    /**
     * Starts new tag.
     * 
     * @param name
     *            tag name
     * @param ns
     *            namespace prefix
     * @return self
     */
    public XmlBuilder start(final String name, final String ns) {
        return start(getQuilifiedName(name, ns));
    }

    /**
     * Starts new tag.
     * 
     * @param name
     *            tag name
     * @param ns
     *            namespace prefix
     * @param uri
     *            namespace uri
     * @return self
     */
    public XmlBuilder start(final String name, final String ns, final String uri) {
        return start(getQuilifiedName(name, ns)).attr(ns, "xmlns", uri);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String value) {
        attrs.put(name, escape(value));
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final boolean value) {
        temp.setLength(0);
        attrs.put(name, temp.append(value).toString());
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final double value) {
        temp.setLength(0);
        attrs.put(name, temp.append(value).toString());
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final float value) {
        temp.setLength(0);
        attrs.put(name, temp.append(value).toString());
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final int value) {
        temp.setLength(0);
        attrs.put(name, temp.append(value).toString());
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final long value) {
        temp.setLength(0);
        attrs.put(name, temp.append(value).toString());
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final Object value) {
        temp.setLength(0);
        attrs.put(name, temp.append(value).toString());
        return this;
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final String value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final boolean value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final double value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final float value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final int value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final long value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given attribute to the opened tag.
     * 
     * @param name
     *            attribute name
     * @param ns
     *            namespace prefix
     * @param value
     *            attribute value
     * @return self
     */
    public XmlBuilder attr(final String name, final String ns, final Object value) {
        return attr(getQuilifiedName(name, ns), value);
    }

    /**
     * Adds the given text to the opened tag.
     * 
     * @param text
     *            text to add
     * @return self
     */
    public XmlBuilder text(final String text) {
        finishDecl();
        if (text != null) {
            buf.append(escape(text));
        }
        return this;
    }

    /**
     * Ends the top tag in stack.
     * 
     * @return self
     */
    public XmlBuilder end() {
        end(stack.peek());
        return this;
    }

    /**
     * Ends opened tags in stack including the given one.
     * 
     * @param name
     *            tag name
     * @return self
     */
    public XmlBuilder end(final String name) {
        finishDecl();
        while (stack.size() > 0) {
            final String top = stack.pop();
            if (!ended) {
                ended = true;
            } else {
                if (prettyPrint) {
                    buf.append('\n');
                    for (int i = 0; i < stack.size(); i++) {
                        buf.append(IDENT);
                    }
                }
            }
            buf.append('<').append('/').append(top).append('>');
            if (top.equals(name)) {
                break;
            }
        }
        return this;
    }

    /**
     * Ends opened tags in stack including the given one.
     * 
     * @param name
     *            tag name
     * @param ns
     *            namespace prefix
     * @return self
     */
    public XmlBuilder end(final String name, final String ns) {
        return end(getQuilifiedName(name, ns));
    }

    /**
     * Adds new element with the given text.
     * 
     * @param name
     *            the tag name
     * @param text
     *            the element text
     * 
     * @return self
     */
    public XmlBuilder element(final String name, final String text) {
        return start(name).text(text).end();
    }

    /**
     * Adds new element with the given text.
     * 
     * @param name
     *            the tag name
     * @param ns
     *            namespace prefix
     * @param text
     *            the element text
     * 
     * @return self
     */
    public XmlBuilder element(final String name, final String ns, final String text) {
        return start(name, ns).text(text).end();
    }

    /**
     * Adds new element with the given text.
     * 
     * @param name
     *            the tag name
     * @param text
     *            the element text
     * 
     * @return self
     */
    public XmlBuilder elementOpt(final String name, final String text) {
        if (LengthUtils.isNotEmpty(text)) {
            return start(name).text(text).end();
        }
        return this;
    }

    /**
     * Adds new element with the given text only if the checking text if empty.
     * 
     * @param name
     *            the tag name
     * @param text
     *            the element text
     * @param textToCheck
     *            the text to check
     * 
     * @return self
     */
    public XmlBuilder elementOptCond(final String name, final String textToCheck, final String text) {
        if (LengthUtils.isEmpty(textToCheck)) {
            if (LengthUtils.isNotEmpty(text)) {
                return start(name).text(text).end();
            }
        }
        return this;
    }

    /**
     * Adds new element with the given text.
     * 
     * @param name
     *            the tag name
     * @param ns
     *            namespace prefix
     * @param text
     *            the element text
     * 
     * @return self
     */
    public XmlBuilder elementOpt(final String name, final String ns, final String text) {
        if (LengthUtils.isNotEmpty(text)) {
            return start(name, ns).text(text).end();
        }
        return this;
    }

    /**
     * Adds new sequence.
     * 
     * @param name
     *            sequence tag name
     * @param strings
     *            sequence of inner strings
     * @param inners
     *            inner tag names
     * @return self
     */
    public XmlBuilder sequence(final String name, final Collection<String> strings, final String... inners) {
        start(name);
        for (final String str : strings) {
            for (final String tag : inners) {
                this.start(tag);
            }
            this.text(str);
            for (final String tag : inners) {
                this.end(tag);
            }
        }
        end(name);
        return this;
    }

    /**
     * Adds new sequence.
     * 
     * @param name
     *            sequence tag name
     * @param ns
     *            namespace prefix
     * @param strings
     *            sequence of inner strings
     * @param inners
     *            inner tag names
     * @return self
     */
    public XmlBuilder sequence(final String name, final String ns, final Collection<String> strings, final String... inners) {
        start(name, ns);
        for (final String str : strings) {
            for (final String tag : inners) {
                this.start(tag, ns);
            }
            this.text(str);
            for (final String tag : inners) {
                this.end(tag, ns);
            }
        }
        end(name, ns);
        return this;
    }

    /**
     * Adds new sequence.
     * 
     * @param name
     *            sequence tag name
     * @param strings
     *            sequence of inner strings
     * @param inners
     *            inner tag names
     * @return self
     */
    public XmlBuilder sequenceOpt(final String name, final Collection<String> strings, final String... inners) {
        if (LengthUtils.isNotEmpty(strings)) {
            start(name);
            for (final String str : strings) {
                for (final String tag : inners) {
                    this.start(tag);
                }
                this.text(str);
                for (final String tag : inners) {
                    this.end(tag);
                }
            }
            end(name);
        }
        return this;
    }

    /**
     * Adds new sequence.
     * 
     * @param name
     *            sequence tag name
     * @param ns
     *            namespace prefix
     * @param strings
     *            sequence of inner strings
     * @param inners
     *            inner tag names
     * @return self
     */
    public XmlBuilder sequenceOpt(final String name, final String ns, final Collection<String> strings, final String... inners) {
        if (LengthUtils.isNotEmpty(strings)) {
            start(name, ns);
            for (final String str : strings) {
                for (final String tag : inners) {
                    this.start(tag, ns);
                }
                this.text(str);
                for (final String tag : inners) {
                    this.end(tag, ns);
                }
            }
            end(name, ns);
        }
        return this;
    }

    /**
     * Ends all opened tags and return raw XML text.
     * 
     * @return raw XML text
     */
    public String finish() {
        end(null);
        return buf.toString();
    }

    /**
     * Finish tag declaration.
     */
    protected void finishDecl() {
        if (inDecl) {
            inDecl = false;
            final String name = stack.peek();

            if (prettyPrint) {
                buf.append('\n');
                for (int i = 0; i < stack.size() - 1; i++) {
                    buf.append(IDENT);
                }
            }

            buf.append('<').append(name);
            if (attrs.size() > 0) {
                for (final Map.Entry<String, String> attr : attrs.entrySet()) {
                    buf.append(" ");
                    buf.append(attr.getKey());
                    buf.append('=');
                    buf.append('"');
                    buf.append(attr.getValue());
                    buf.append('"');
                }
            }
            buf.append('>');

            attrs.clear();
        }
    }

    /**
     * Returns fully qualified tag name
     * 
     * @param name
     *            tag name
     * @param ns
     *            namespace prefix
     * @return string
     */
    protected String getQuilifiedName(final String name, final String ns) {
        if (LengthUtils.isEmpty(name)) {
            return ns;
        }
        if (LengthUtils.isEmpty(ns)) {
            return name;
        }
        return ns + ":" + name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return buf.toString();
    }

    public static String escape(final String text) {
        final Matcher m = ESCAPED.matcher(text);
        final StringBuffer buf = new StringBuffer();
        while (m.find()) {
            final String s = m.group();
            final char ch = s.charAt(0);
            final String repl = "&#" + Integer.toString(ch) + ";";
            m.appendReplacement(buf, repl);
        }
        m.appendTail(buf);
        return buf.toString();
    }
}
