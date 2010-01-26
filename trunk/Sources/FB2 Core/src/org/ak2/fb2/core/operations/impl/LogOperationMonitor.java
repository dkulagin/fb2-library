package org.ak2.fb2.core.operations.impl;

import org.ak2.fb2.core.operations.IOperationMonitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogOperationMonitor implements IOperationMonitor {

    private static final Log LOGGER = LogFactory.getLog(LogOperationMonitor.class);

    private int fieldTotalWork;

    private int fieldExecuted;

    private int fieldLastShownPercent;

    private String fieldMainTaskName;

    public void beginTask(final String name, final int totalWork) {
        fieldMainTaskName = name;
        fieldTotalWork = totalWork;
        fieldExecuted = 0;
        fieldLastShownPercent = -100;
        LOGGER.info("Task [" + fieldMainTaskName + "] started");
    }

    public void done() {
        LOGGER.info("Task [" + fieldMainTaskName + "] finished");
    }

    public boolean isCanceled() {
        return false;
    }

    public void setTaskName(final String name) {
    }

    public void subTask(final String name) {
        LOGGER.info("\tSubtask [" + name + "] started");
    }

    public void worked(final int work) {
        if (fieldExecuted < fieldTotalWork) {
            fieldExecuted = Math.min(fieldExecuted + work, fieldTotalWork);
            final int newPercents = getPercent(fieldExecuted);
            if (newPercents - fieldLastShownPercent >= 1) {
                LOGGER.info("\tProgress: " + newPercents + "%");
                fieldLastShownPercent = newPercents;
            }
        }
    }

    private int getPercent(final int value) {
        return (int)Math.min(100, Math.floor(100.0 * value / fieldTotalWork));
    }

}
