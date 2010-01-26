package org.ak2.fb2.core.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

public class FileUtils {

    public static String getFileName(final String original, final String ext) {
        return FilenameUtils.removeExtension(original) + "." + ext;
    }

    public static void copyFile(final File target, final File source) throws IOException {
        org.apache.commons.io.FileUtils.copyFile(source, target);
    }

    public static void deleteDirectory(final File dir) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(dir);
    }

}
