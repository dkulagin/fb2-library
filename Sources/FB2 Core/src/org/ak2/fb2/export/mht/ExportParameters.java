package org.ak2.fb2.export.mht;

import java.io.File;
import java.text.MessageFormat;

import org.ak2.fb2.core.bookstore.IBook;
import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.operations.IOperationParameters;
import org.ak2.fb2.core.utils.LengthUtils;

public class ExportParameters implements IOperationParameters {

    private static final String CACHED_XML_FILENAME = ".mht.xml";

    private static final String CACHED_HTML_FILENAME = "{0}.mht.html";

    private static final String CACHED_MHT_FILENAME = "{0}.mht";

    private IFileCach fieldCash;

    private IBook fieldDescriptor;

    private String fieldResultFileName;

    private String fieldHtmlEncoding = "windows-1251";

    private ContentTransferEncoding fieldContentEncoding = ContentTransferEncoding.Base64;

    public boolean isValid() {
        return fieldCash != null && fieldDescriptor != null && LengthUtils.isNotEmpty(fieldResultFileName);
    }

    public void release() {
        fieldCash = null;
        fieldDescriptor = null;
    }

    /**
     * @return the descriptor
     */
    public final IBook getDescriptor() {
        return fieldDescriptor;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public final void setDescriptor(final IBook descriptor) {
        fieldDescriptor = descriptor;
    }

    /**
     * @return the cash
     */
    public final IFileCach getCach() {
        return fieldCash;
    }

    /**
     * @param cash the cash to set
     */
    public final void setCash(final IFileCach cash) {
        fieldCash = cash;
    }

    /**
     * @return the resultFileName
     */
    public final String getResultFileName() {
        return fieldResultFileName;
    }

    /**
     * @param resultFileName the resultFileName to set
     */
    public final void setResultFileName(final String resultFileName) {
        fieldResultFileName = resultFileName;
    }

    /**
     * @return the htmlEncoding
     */
    public final String getHtmlEncoding() {
        return fieldHtmlEncoding;
    }

    /**
     * @param htmlEncoding the htmlEncoding to set
     */
    public final void setHtmlEncoding(final String htmlEncoding) {
        fieldHtmlEncoding = htmlEncoding;
    }

    /**
     * @return the contentEncoding
     */
    public final ContentTransferEncoding getContentEncoding() {
        return fieldContentEncoding;
    }

    /**
     * @param contentEncoding the contentEncoding to set
     */
    public final void setContentEncoding(final ContentTransferEncoding contentEncoding) {
        fieldContentEncoding = contentEncoding;
    }

    public File getCachedXmlFile() {
        return getCachedFile(CACHED_XML_FILENAME);
    }

    public File getCachedHtmlFile() {
        return getCachedFile(CACHED_HTML_FILENAME);
    }

    public File getCachedMhtFile() {
        return getCachedFile(CACHED_MHT_FILENAME);
    }

    protected File getCachedFile(final String pattern) {
        final IFileCach cach = getCach();
        final String digest = getDescriptor().getDigest();
        final String fileName = MessageFormat.format(pattern, getHtmlEncoding());
        return cach.getCachedFile(digest, fileName);
    }
}
