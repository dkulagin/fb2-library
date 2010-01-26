package org.ak2.fb2.export.mht;

public enum ContentTransferEncoding {

    Base64("base64"), QuotedPrintable("Quoted-Printable");

    private String fieldMimeString;

    private ContentTransferEncoding(final String mimeString) {
        fieldMimeString = mimeString;
    }

    @Override
    public String toString() {
        return fieldMimeString;
    }
}
