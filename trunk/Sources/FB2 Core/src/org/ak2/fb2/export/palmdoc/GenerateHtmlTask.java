package org.ak2.fb2.export.palmdoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.core.utils.ResourceUtils;
import org.apache.commons.io.FileUtils;

public class GenerateHtmlTask {

    private static final String FB2_2_HTML_RU_XSL = ResourceUtils.getPackageResource("/", GenerateHtmlTask.class, "/xsl/FB2_2_html_ru.xsl");

    private final ExportParameters fieldParams;

    private final ExportContext fieldContext;

    public GenerateHtmlTask(final ExportParameters params, final ExportContext context) {
        fieldParams = params;
        fieldContext = context;
    }

    public static int getWorkUnits() {
        return 3;
    }

    public void execute(final IOperationMonitor monitor) throws TransformerFactoryConfigurationError, TransformerException, IOException {
        monitor.subTask("Generate html...");

        if (!loadFromCach()) {

            transform();
            monitor.worked(1);

            correctHREFs();
            monitor.worked(1);

            saveToCach(monitor);
            monitor.worked(1);

        } else {
            monitor.worked(getWorkUnits());
        }
    }

    private boolean loadFromCach() throws IOException {
        final IFileCach cach = fieldParams.getCach();
        if (cach != null) {
            final File cachedFile = fieldParams.getCachedHtmlFile();
            if (cachedFile != null && cachedFile.exists()) {
                final String content = FileUtils.readFileToString(cachedFile, fieldParams.getHtmlEncoding());
                fieldContext.setContent(content);
            }
        }
        return false;
    }

    private void saveToCach(final IOperationMonitor monitor) throws IOException {
        final IFileCach cach = fieldParams.getCach();
        if (cach != null) {
            final File cachedFile = fieldParams.getCachedHtmlFile();
            FileUtils.writeStringToFile(cachedFile, fieldContext.getContent(), fieldParams.getHtmlEncoding());
        }
    }

    protected void transform() throws TransformerFactoryConfigurationError, TransformerException,
            UnsupportedEncodingException {

        final InputStream resourceAsStream = getClass().getResourceAsStream(FB2_2_HTML_RU_XSL);
        
        final StreamSource xslForHtml = new StreamSource(resourceAsStream);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final DOMSource fb2FileSource = new DOMSource(fieldContext.getDocument());

        final StreamResult htmlFile = new StreamResult(output);

        final Transformer t = TransformerFactory.newInstance().newTransformer(xslForHtml);
        t.setOutputProperty("encoding", fieldParams.getHtmlEncoding());

        t.transform(fb2FileSource, htmlFile);

        final String content = new String(output.toByteArray(), fieldParams.getHtmlEncoding());

        //String content = new String(output.toByteArray(), "UTF-8");
        //content = content.replaceAll("charset=UTF-8", "charset=" + fieldParams.getHtmlEncoding());

        fieldContext.setContent(content);
    }

    protected void correctHREFs() {
        String content = fieldContext.getContent();
        final HashMap<String, String> fieldHrefs = fieldContext.getHrefs();

        int index = content.indexOf("<a name=\"", 0);

        while (index != -1) {
            String xrefName = content.substring(index + 9, content.indexOf("\"", index + 9));
            while (xrefName.startsWith("#")) {
                xrefName = xrefName.substring(1);
            }

            final int hrefIndex = index;

            final String xrefValue = generateXrefValue(hrefIndex);

            index = content.indexOf("<a name=\"", index + 10 + xrefName.length());

            fieldHrefs.put(xrefName, xrefValue);
        }

        int maxIndex = 0;

        for (final String name : fieldHrefs.keySet()) {
            final String searchPattern1 = "<a filepos=\"000000000\" href=\"#" + name + "\"";
            final String searchPattern2 = "<a href=\"#" + name + "\" filepos=\"000000000\"";

            int index1 = content.indexOf(searchPattern1);
            if (index1 == -1) {
                index1 = content.indexOf(searchPattern2);
            }
            if (index1 > maxIndex) {
                maxIndex = index1;
            }
        }

        String header = content.substring(0, maxIndex + 100);

        for (final String name : fieldHrefs.keySet()) {
            final String value = fieldHrefs.get(name);

            final String searchPattern1 = "<a filepos=\"000000000\" href=\"#" + name + "\"";
            final String searchPattern2 = "<a href=\"#" + name + "\" filepos=\"000000000\"";

            final String replaceString = "<A HREF=\"#" + name + "\" filepos=\"" + value + "\"";
            int index1 = header.indexOf(searchPattern1);
            if (index1 == -1) {
                index1 = header.indexOf(searchPattern2);
            }
            if (index1 > 0) {
                header = header.substring(0, index1) + replaceString
                        + header.substring(index1 + searchPattern1.length());
            }
        }

        content = header + content.substring(maxIndex + 100);

        fieldContext.setContent(content);
    }

    /**
     * @param index
     * @return
     */
    private static String generateXrefValue(final int index) {
        final int idx = index;
        String result = "" + (idx);
        final int l = result.length();
        for (int i = 0; i < 9 - l; i++) {
            result = "0" + result;
        }
        return result;
    }
}
