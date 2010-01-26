package org.ak2.fb2.export.mht;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.ak2.fb2.core.fictionbook.image.FictionBookImage;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.apache.commons.io.IOUtils;

public class GenerateMhtTask {

    private final ExportParameters fieldParams;

    private final ExportContext fieldContext;

    public GenerateMhtTask(final ExportParameters params, final ExportContext context) {
        fieldParams = params;
        fieldContext = context;
    }

    public static int getWorkUnits() {
        return 4;
    }

    public void execute(final IOperationMonitor monitor) throws IOException, MessagingException {
        monitor.subTask("Generate mht...");

        final File resultFile = new File(fieldParams.getResultFileName());
        resultFile.getParentFile().mkdirs();
        resultFile.delete();

        monitor.worked(1);

        // Instantiate a Multipart object
        final MimeMultipart mp = new MimeMultipart("related");
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        final MimeMessage msg = new MimeMessage(session);

        // Add HTML part
        mp.addBodyPart(createHtmlPart());
        monitor.worked(1);

        final File[] imageFiles = fieldContext.getImageFiles();
        for (int i = 0; i < imageFiles.length; i++) {
            mp.addBodyPart(createImagePart(imageFiles[i]));
        }

        monitor.worked(1);

        msg.setContent(mp);

        final FileOutputStream out = new FileOutputStream(resultFile);
        msg.writeTo(out);
        IOUtils.closeQuietly(out);

        monitor.worked(1);
    }

    private MimeBodyPart createHtmlPart() throws MessagingException {
        final String fb2FileName = fieldParams.getDescriptor().getSource().getBookFileName();

        final MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setText(fieldContext.getContent());

        htmlPart.addHeader("Content-Type", "text/html");
        htmlPart.addHeader("Content-Location", fb2FileName);
        htmlPart.addHeader("Content-Transfer-Encoding", fieldParams.getContentEncoding().toString());
        htmlPart.setDisposition("inline;");
        htmlPart.setFileName(fb2FileName);

        return htmlPart;
    }

    private MimeBodyPart createImagePart(final File imageFile) throws MessagingException {
        final String imageFileName = imageFile.getName();
        final FictionBookImage image = fieldContext.getBook().getImage(imageFileName);

        final MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.setDataHandler(new DataHandler(new FileDataSource(imageFile)));

        imagePart.addHeader("Content-Type", image.getImageMineType());
        imagePart.addHeader("Content-Location", imageFileName);
        imagePart.setDisposition("inline;");
        imagePart.setFileName(imageFileName);

        return imagePart;
    }

}
