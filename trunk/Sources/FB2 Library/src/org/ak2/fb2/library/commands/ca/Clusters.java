/**
 *
 */
package org.ak2.fb2.library.commands.ca;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.ak2.utils.LengthUtils;

class Clusters {

    private final Map<Author, List<Set<Author>>> clusterMap = new TreeMap<Author, List<Set<Author>>>();

    private final Set<Set<Author>> clusterList = new LinkedHashSet<Set<Author>>();

    public Clusters(final Author[] authors, final int dist) {
        for (int i = 0; i < authors.length; i++) {
            for (int j = i + 1; j < authors.length; j++) {
                if (Author.isSimilar(authors[i], authors[j], dist)) {
                    addAuthors(authors[i], authors[j]);
                }
            }
        }
        compress();
    }

    public Iterable<Set<Author>> getClusters() {
        return clusterList;
    }

    protected Iterable<Set<Author>> compress() {
        boolean found = false;
        do {
            found = false;
            for (final Map.Entry<Author, List<Set<Author>>> entry : clusterMap.entrySet()) {
                final Author author = entry.getKey();
                final List<Set<Author>> list = entry.getValue();
                if (author.isShortFirstName()) {
                    list.clear();
                } else if ((list.size() > 1)) {
                    System.out.println("Compress: " + list);
                    found = true;
                    final Set<Author> cluster = new TreeSet<Author>();
                    for (final Set<Author> pair : list) {
                        cluster.addAll(pair);
                        for (final Author other : pair) {
                            if (!author.equals(other)) {
                                final List<Set<Author>> otherList = clusterMap.get(other);
                                if (otherList != null) {
                                    otherList.remove(pair);
                                }
                            }
                        }
                    }
                    list.clear();
                    list.add(cluster);
                    for (final Author other : cluster) {
                        if (!author.equals(other)) {
                            final List<Set<Author>> otherList = clusterMap.get(other);
                            if (otherList != null) {
                                otherList.add(cluster);
                            }
                        }
                    }
                    break;
                }
            }
        } while (found);

        for (final List<Set<Author>> list : clusterMap.values()) {
            if (LengthUtils.isNotEmpty(list)) {
                clusterList.add(list.get(0));
            }
        }

        return getClusters();

    }

    protected void addAuthors(final Author authorI, final Author authorJ) {
        System.out.println("Create pair for '" + authorI + "' and '" + authorJ + "'");

        final Set<Author> pair = new HashSet<Author>();
        pair.add(authorI);
        pair.add(authorJ);

        if (!authorI.isShortFirstName()) {
            List<Set<Author>> listI = clusterMap.get(authorI);
            if (listI == null) {
                listI = new LinkedList<Set<Author>>();
                clusterMap.put(authorI, listI);
            }
            listI.add(pair);
        }

        if (!authorJ.isShortFirstName()) {
            List<Set<Author>> listJ = clusterMap.get(authorJ);
            if (listJ == null) {
                listJ = new LinkedList<Set<Author>>();
                clusterMap.put(authorJ, listJ);
            }
            listJ.add(pair);
        }
    }

    public static String toString(final Set<Author> cluster) {
        final StringBuilder buf = new StringBuilder();
        for (final Author a : cluster) {
            buf.append("'").append(a.getName()).append("'");
            buf.append(": ");
            buf.append("'").append(a.getFolder().getAbsolutePath()).append("'");
            buf.append("\n");
        }
        return buf.toString();
    }

}