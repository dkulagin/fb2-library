package org.ak2.fb2.core.operations.exceptions;

import org.ak2.fb2.core.operations.ITask;

public class TaskExecutionException extends Exception {

    private static final long serialVersionUID = -1882478297065902504L;

    private final transient ITask<?,?> fieldSource;

    public TaskExecutionException(final ITask<?,?> source, final Throwable th) {
        super("Task failed", th);
        fieldSource = source;
    }

    /**
     * @return the source
     */
    public final ITask<?,?> getSource() {
        return fieldSource;
    }
}
