package org.ak2.fb2.core.operations.impl;

import org.ak2.fb2.core.operations.IOperationMonitor;

public class ConsoleOperationMonitor implements IOperationMonitor {

    private int fieldTotalWork;

    private int fieldExecuted;

    private int fieldLastShownPercent;

    private String fieldMainTaskName;

    public void beginTask(final String name, final int totalWork) {
        fieldMainTaskName = name;
        fieldTotalWork = totalWork;
        fieldExecuted = 0;
        fieldLastShownPercent = -100;
        System.out.println("Task [" + fieldMainTaskName + "] started");
    }

    public void done() {
        System.out.println("Task [" + fieldMainTaskName + "] finished");
    }

    public boolean isCanceled() {
        return false;
    }

    public void setTaskName(final String name) {
    }

    public void subTask(final String name) {
        System.out.println("\tSubtask [" + name + "] started");
    }

    public void worked(final int work) {
        if (fieldExecuted < fieldTotalWork) {
            fieldExecuted = Math.min(fieldExecuted + work, fieldTotalWork);
            final int newPercents = getPercent(fieldExecuted);
            if (newPercents - fieldLastShownPercent >= 1) {
                System.out.println("\tProgress: " + newPercents + "%");
                fieldLastShownPercent = newPercents;
            }
        }
    }

    private int getPercent(final int value) {
        return (int)Math.min(100, Math.floor(100.0 * value / fieldTotalWork));
    }

}
