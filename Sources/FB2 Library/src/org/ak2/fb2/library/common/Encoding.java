package org.ak2.fb2.library.common;

public enum Encoding {

    /**
     * UTF-8 encoding.
     */
    UTF8("Utf-8", 0x20, 0xD0, 0xB8, 0x20),
    /**
     * Windows 1251 encoding.
     */
    CP1251("Windows-1251", 0x20, 0xE8, 0x20);

    private final String xmlName;

    private final int[] pattern;

    private Encoding(String xmlName, int... pattern) {
        this.xmlName = xmlName;
        this.pattern = pattern;
    }

    /**
     * @return the xmlName
     */
    public final String getXmlName() {
        return xmlName;
    }

    /**
     * @return the pattern
     */
    public final int[] getPattern() {
        return pattern;
    }
}
