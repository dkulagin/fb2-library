/**
 *
 */
package org.ak2.fb2.core.fictionbook.source;

public class ScanOptions {

    private boolean scanFolders;

    private boolean scanZipFiles;

    private boolean scanZipFolders;

    /**
     * The Constructor.
     */
    public ScanOptions() {

    }

    /**
     * The Constructor.
     *
     * @param scanZipFolders scanning zip folders enabled
     * @param scanZipFiles scanning zip files enabled
     * @param scanFolders scanning folders enabled
     */
    public ScanOptions(final boolean scanFolders, final boolean scanZipFiles, final boolean scanZipFolders) {
        super();
        this.scanFolders = scanFolders;
        this.scanZipFiles = scanZipFiles;
        this.scanZipFolders = scanZipFolders;
    }

    /**
     * @return the scanFolders
     */
    public final boolean isScanFolders() {
        return scanFolders;
    }

    /**
     * @param scanFolders the scanFolders to set
     */
    public final void setScanFolders(final boolean scanFolders) {
        this.scanFolders = scanFolders;
    }

    /**
     * @return the scanZipFiles
     */
    public final boolean isScanZipFiles() {
        return scanZipFiles;
    }

    /**
     * @param scanZipFiles the scanZipFiles to set
     */
    public final void setScanZipFiles(final boolean scanZipFiles) {
        this.scanZipFiles = scanZipFiles;
    }

    /**
     * @return the scanZipFolders
     */
    public final boolean isScanZipFolders() {
        return scanZipFolders;
    }

    /**
     * @param scanZipFolders the scanZipFolders to set
     */
    public final void setScanZipFolders(final boolean scanZipFolders) {
        this.scanZipFolders = scanZipFolders;
    }
}