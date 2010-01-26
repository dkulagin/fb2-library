package org.ak2.fb2.export.mht;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
        return 2;
    }

    public void execute(final IOperationMonitor monitor) throws TransformerFactoryConfigurationError,
            TransformerException, IOException {
        monitor.subTask("Generate html...");

        if (!loadFromCach()) {

            transform();
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
                return true;
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

        final StreamSource xslForHtml = new StreamSource(getClass().getResourceAsStream(FB2_2_HTML_RU_XSL));

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
}
