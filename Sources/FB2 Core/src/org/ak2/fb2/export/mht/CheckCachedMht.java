package org.ak2.fb2.export.mht;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.core.operations.ITask;
import org.ak2.fb2.core.operations.exceptions.TaskExecutionException;
import org.ak2.fb2.core.operations.impl.AbstractTask;
import org.ak2.fb2.core.utils.FileUtils;

public class CheckCachedMht extends AbstractTask<ExportParameters, ExportContext> {

    public Collection<ITask<ExportParameters, ExportContext>> execute(final IOperationMonitor monitor) throws TaskExecutionException {

        try {
            final File cachedFile = getParameters().getCachedMhtFile();
            if (cachedFile != null && cachedFile.exists()) {
                final File resultFile = new File(getParameters().getResultFileName());
                monitor.subTask("Copy cached mht file...");
                FileUtils.copyFile(resultFile, cachedFile);
                monitor.worked(1);
                return null;
            }
        } catch (final IOException ex) {
            throw new TaskExecutionException(this, ex);
        }

        // TODO Add tasks
/*
        addTask(new PrepareFictionBookTask());
        addTask(new GenerateHtmlTask());
        addTask(new GenerateMhtTask());
*/
        return getNextTasks();
    }

}
