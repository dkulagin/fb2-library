package org.ak2.fb2.core.operations.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.ak2.fb2.core.operations.IOperationContext;
import org.ak2.fb2.core.operations.IOperationParameters;
import org.ak2.fb2.core.operations.ITask;

public abstract class AbstractTask<Parameters extends IOperationParameters, Context extends IOperationContext>
        implements ITask<Parameters, Context> {

    private Parameters fieldParameters;

    private Context fieldContext;

    private final LinkedList<ITask<Parameters, Context>> fieldNextTasks = new LinkedList<ITask<Parameters,Context>>();

    /**
     * @return the context
     */
    public final Context getContext() {
        return fieldContext;
    }

    /**
     * @return the parameters
     */
    public final Parameters getParameters() {
        return fieldParameters;
    }

    /**
     * @param context the context to set
     */
    public final void setContext(final Context context) {
        fieldContext = context;
    }

    /**
     * @param parameters the parameters to set
     */
    public final void setParameters(final Parameters parameters) {
        fieldParameters = parameters;
    }

    /**
     * @see org.ak2.fb2.core.operations.ITask#release()
     */
    public final void release() {
        setContext(null);
        setParameters(null);
    }

    protected final void addTask(final ITask<Parameters, Context> nextTask) {
        fieldNextTasks.add(nextTask);
    }

    protected final Collection<ITask<Parameters, Context>> getNextTasks() {
        return fieldNextTasks;
    }
}
