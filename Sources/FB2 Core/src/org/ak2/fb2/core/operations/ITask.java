package org.ak2.fb2.core.operations;

import java.util.Collection;

import org.ak2.fb2.core.operations.exceptions.TaskExecutionException;

public interface ITask<Parameters extends IOperationParameters, Context extends IOperationContext> {

    Collection<ITask<Parameters, Context>> execute(final IOperationMonitor monitor) throws TaskExecutionException;

    /**
     * @return the context
     */
    Context getContext();

    /**
     * @return the parameters
     */
    Parameters getParameters();

    /**
     * @param context the context to set
     */
    void setContext(Context context);

    /**
     * @param parameters the parameters to set
     */
    void setParameters(Parameters parameters);

    /**
     * Releases the task
     */
    void release();

}

