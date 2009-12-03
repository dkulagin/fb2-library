package org.ak2.fb2.library.common;

import java.io.File;
import java.io.IOException;

import org.ak2.utils.LengthUtils;

public enum OutputPath {

    Simple {
        /**
         * @see org.ak2.fb2.library.common.OutputPath#getFolder(java.io.File, java.lang.String, java.lang.String)
         */
        @Override
        public File getFolder(File outFolder, String author, String sequence) {
            return outFolder;
        }
    },

    Standard {
        /**
         * @see org.ak2.fb2.library.common.OutputPath#getFolder(java.io.File, java.lang.String, java.lang.String)
         */
        @Override
        public File getFolder(File outFolder, String author, String sequence) throws IOException {
            return createFolder(outFolder, author, sequence);
        }
    },

    Library {
        /**
         * @see org.ak2.fb2.library.common.OutputPath#getFolder(java.io.File, java.lang.String, java.lang.String)
         */
        @Override
        public File getFolder(File outFolder, String author, String sequence) throws IOException {
            String subTree = "";
            char first = author.charAt(0);
            if (Character.isDigit(first)) {
                subTree = "0-9";
            } else if (Character.isLetter(first)) {
                subTree = "" + first;
            } else {
                subTree = "Other";
            }
            return createFolder(outFolder, subTree, author, sequence);
        }
    };

    public abstract File getFolder(File outFolder, String author, String sequence) throws IOException;

    private static File createFolder(File outFolder, String... segments) throws IOException {
        File current = outFolder;
        for (String segment : segments) {
            if (LengthUtils.isNotEmpty(segment)) {
                current = new File(current, segment);
                current.mkdir();
                if (!current.exists()) {
                    throw new IOException("The folder " + current.getAbsolutePath() + " has  not been created");
                }
            }
        }
        return current;
    }

}
