package org.ak2.fb2.library.common;

import java.io.File;

public class ProcessingException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5159027429049447546L;

    private final ProcessingResult result;

    private final File file;

    public ProcessingException(File file) {
        this.result = ProcessingResult.DUPLICATED;
        this.file = file;
    }

    public ProcessingException(Throwable cause) {
        super(cause);
        this.result = ProcessingResult.FAILED;
        this.file = null;
    }

    public ProcessingResult getResult() {
        return result;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        String s = getClass().getName() + ": result=" + result + " file=" + file;
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
