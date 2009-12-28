package org.ak2.utils.files;

import java.io.File;
import java.io.FileFilter;

public class FolderScanner {

    public static void enumerateWide(final File root, final FileFilter filter, final int depth) {
        File[] folders = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return filter.accept(file);
                }
                return false;
            }
        });
        int newDepth = depth - 1;
        if (newDepth >= 0) {
            for (File folder : folders) {
                enumerateWide(folder, filter, newDepth);
            }
        }
    }

    public static void enumerateWide(final File root, final FileFilter filter, final FileFilter worker, final int depth) {
        if (filter.accept(root)) {
            File[] folders = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        return filter.accept(file);
                    }
                    return false;
                }
            });
            int newDepth = depth - 1;
            if (newDepth >= 0) {
                for (File folder : folders) {
                    enumerateWide(folder, filter, worker, newDepth);
                }
            }
            worker.accept(root);
        }
    }

    public static void enumerateDepth(final File root, final FileFilter filter, final int depth) {
        root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    boolean accept = filter.accept(file);
                    if (accept) {
                        int newDepth = depth - 1;
                        if (newDepth >= 0) {
                            enumerateDepth(file, filter, newDepth);
                        }
                    }
                }
                return false;
            }
        });
    }
}