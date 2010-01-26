package org.ak2.fb2.export.palmdoc;

import java.io.File;
import java.io.IOException;

import org.ak2.fb2.core.bookstore.IFileCach;
import org.ak2.fb2.core.operations.IOperation;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.core.utils.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExportPalmDoc implements IOperation {

    private static final Log LOGGER = LogFactory.getLog(ExportPalmDoc.class);

    private final ExportParameters fieldParams;

    private final ExportContext fieldContext = new ExportContext();

    public ExportPalmDoc(final ExportParameters params) {
        fieldParams = params;
    }

    public static int getWorkUnits() {
        return 1 + PrepareFictionBookTask.getWorkUnits() + GenerateHtmlTask.getWorkUnits()
                + GeneratePrcTask.getWorkUnits();
    }

    /**
     * @see org.ak2.fb2.core.operations.IOperation#execute(org.ak2.fb2.core.operations.IOperationMonitor)
     */
    public void execute(final IOperationMonitor monitor) {
        monitor.beginTask("Export to PalmDoc", getWorkUnits());
        try {
            if (!copyFromCach(monitor)) {
                final PrepareFictionBookTask xmlTask = new PrepareFictionBookTask(fieldParams, fieldContext);
                xmlTask.execute(monitor);

                final GenerateHtmlTask htmlTask = new GenerateHtmlTask(fieldParams, fieldContext);
                htmlTask.execute(monitor);

                final GeneratePrcTask prcTask = new GeneratePrcTask(fieldParams, fieldContext);
                prcTask.execute(monitor);

                copyToCach(monitor);
            }

        } catch (final Throwable th) {
            LOGGER.error("", th);
        } finally {
            monitor.done();
            fieldParams.release();
            fieldContext.release();
        }
    }

    private boolean copyFromCach(final IOperationMonitor monitor) throws IOException {
        final IFileCach cach = fieldParams.getCach();
        if (cach != null) {
            final File cachedFile = fieldParams.getCachedPrcFile();
            if (cachedFile != null && cachedFile.exists()) {
                final File resultFile = new File(fieldParams.getResultFileName());
                monitor.subTask("Copy cached prc file...");
                FileUtils.copyFile(resultFile, cachedFile);
                monitor.worked(1);
                return true;
            }
        }
        return false;
    }

    private boolean copyToCach(final IOperationMonitor monitor) throws IOException {
        final IFileCach cach = fieldParams.getCach();
        if (cach != null) {
            final File cachedFile = fieldParams.getCachedPrcFile();
            if (cachedFile != null) {
                final File resultFile = new File(fieldParams.getResultFileName());
                monitor.subTask("Save generated prc file into cach...");
                FileUtils.copyFile(cachedFile, resultFile);
                monitor.worked(1);
                return true;
            }
        }
        return false;
    }

}