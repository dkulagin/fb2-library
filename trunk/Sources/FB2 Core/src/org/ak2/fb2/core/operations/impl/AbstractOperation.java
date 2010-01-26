package org.ak2.fb2.core.operations.impl;

import java.util.Collection;

import org.ak2.fb2.core.operations.IOperation;
import org.ak2.fb2.core.operations.IOperationContext;
import org.ak2.fb2.core.operations.IOperationMonitor;
import org.ak2.fb2.core.operations.IOperationParameters;
import org.ak2.fb2.core.operations.ITask;
import org.ak2.fb2.core.operations.exceptions.TaskExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractOperation<Parameters extends IOperationParameters, Context extends IOperationContext> implements IOperation {

    private final Log LOGGER = LogFactory.getLog(this.getClass());

    private final Parameters fieldParams;

    private final Context fieldContext;

    protected AbstractOperation(final Parameters params, final Context context) {
        fieldParams = params;
        fieldContext = context;
    }

    /**
     * @see org.ak2.fb2.core.operations.IOperation#execute(org.ak2.fb2.core.operations.IOperationMonitor)
     */
    public void execute(final IOperationMonitor monitor) {
        final Collection<ITask<Parameters, Context>> mainTasks = getMainTasks();
        // FIXME Calculate right value
        monitor.beginTask(getName(), 100);
        try {
            execute(mainTasks, monitor);
        } catch (TaskExecutionException ex) {
            LOGGER.error("", ex);
        } finally {
            monitor.done();
            fieldParams.release();
            fieldContext.release();
        }
    }

    protected void execute(final Iterable<ITask<Parameters, Context>> tasks, final IOperationMonitor monitor) throws TaskExecutionException {
        for (final ITask<Parameters, Context> task : tasks) {
            task.setParameters(fieldParams);
            task.setContext(fieldContext);
            final Iterable<ITask<Parameters, Context>> innerTasks = task.execute(monitor);
            execute(innerTasks, monitor);
        }
    }

    protected abstract String getName();

    protected abstract Collection<ITask<Parameters, Context>> getMainTasks();
}