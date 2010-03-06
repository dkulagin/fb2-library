package org.ak2.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexander Kasatkin
 */
public final class FileUtils
{
    /**
     * Constructor
     */
    private FileUtils()
    {
    }

    /**
     * Normalize file name.
     *
     * @param fileName original file name
     * @return string
     */
    public static String normalizeFileName(final String fileName)
    {
        try
        {
            if (LengthUtils.isNotEmpty(fileName))
            {
                return fileName.replaceAll("[\\\\\\/]", "\\" + File.separator);
            }
        }
        catch (Exception e)
        {
            System.err.println("normalizeFileName(" + fileName + ") failed: " + e.getMessage());
        }
        return fileName;
    }

    /**
     * Builds file path relative to the current directory
     *
     * @param original original file
     * @return relative file path
     */
    public static String getRelativeFileName(final File original)
    {
        return getRelativeFileName(original, new File("."));
    }

    /**
     * Builds file path relative to the given file
     *
     * @param original original file
     * @param base base directory
     * @return relative file path
     */
    public static String getRelativeFileName(final File original, final File base)
    {
        try
        {
            final String fileAbsolutePath = original.getCanonicalPath();
            final String currentAbsolutePath = base.getCanonicalPath();
            if (fileAbsolutePath.startsWith(currentAbsolutePath))
            {
                return "." + fileAbsolutePath.substring(currentAbsolutePath.length());
            }
            else
            {
                return fileAbsolutePath;
            }
        }
        catch (IOException ex)
        {
            return original.getAbsolutePath();
        }
    }

    /**
     * Builds file path relative to the given file
     *
     * @param original original file
     * @param base base directory
     * @return relative file path
     */
    public static String getRelativeFileName(final String original, final File base)
    {
        try
        {
            final String fileAbsolutePath = original;
            final String currentAbsolutePath = base.getCanonicalPath();
            if (fileAbsolutePath.startsWith(currentAbsolutePath))
            {
                return "." + fileAbsolutePath.substring(currentAbsolutePath.length());
            }
            else
            {
                return fileAbsolutePath;
            }
        }
        catch (IOException ex)
        {
            return original;
        }
    }

}
