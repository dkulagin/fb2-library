package org.ak2.utils.files;

import java.io.File;
import java.io.FileFilter;

public class FolderScanner {

    public static void enumerateWide(final File root, final FileFilter filter, final int depth) {
        assert root != null;
        assert depth >= 0;

        final File[] folders = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                if (file.isDirectory()) {
                    return acceptFile(filter, file);
                }
                return false;
            }
        });
        final int newDepth = depth - 1;
        if (newDepth >= 0) {
            for (final File folder : folders) {
                enumerateWide(folder, filter, newDepth);
            }
        }
    }

    public static void enumerateWide(final File root, final FileFilter worker) {
        enumerateWide(root, null, worker, Integer.MAX_VALUE);
    }

    public static void enumerateWide(final File root, final FileFilter filter, final FileFilter worker, final int depth) {
        assert root != null;
        assert depth >= 0;
        assert worker != null;

        if (filter.accept(root)) {
            final File[] folders = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File file) {
                    if (file.isDirectory()) {
                        return acceptFile(filter, file);
                    }
                    return false;
                }
            });
            final int newDepth = depth - 1;
            if (newDepth >= 0) {
                for (final File folder : folders) {
                    enumerateWide(folder, filter, worker, newDepth);
                }
            }
            worker.accept(root);
        }
    }

    public static void enumerateDepth(final File root, final FileFilter filter, final int depth) {
        assert root != null;
        assert depth >= 0;

        root.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                if (file.isDirectory()) {
                    final boolean accept = acceptFile(filter, file);
                    if (accept) {
                        final int newDepth = depth - 1;
                        if (newDepth >= 0) {
                            enumerateDepth(file, filter, newDepth);
                        }
                    }
                }
                return false;
            }
        });
    }

    public static void enumerateDepth(final File root, final FileFilter filter, final FileFilter worker, final int depth, final int barrier) {
        assert root != null;
        assert depth >= 0;
        assert worker != null;

        root.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                if (file.isDirectory()) {
                    final boolean accept = acceptFile(filter, file);
                    if (accept) {
                        if (depth <= barrier) {
                            worker.accept(file);
                        }
                        final int newDepth = depth - 1;
                        if (newDepth >= 0) {
                            enumerateDepth(file, filter, worker, newDepth, barrier);
                        }
                    }
                }
                return false;
            }
        });
    }

    static boolean acceptFile(final FileFilter filter, final File file) {
        return filter != null ? filter.accept(file) : true;
    }
}