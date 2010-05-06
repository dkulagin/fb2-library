package org.ak2.fb2.shelf.catalog;

import java.util.Comparator;

import org.ak2.utils.LengthUtils;

public class BookInfoComparators {

    public static final Comparator<BookInfo> DEFAULT = new BaseBookInfoComparator();

    public static final Comparator<BookInfo> SEQUENCE = new BaseBookInfoComparator() {
        @Override
        public int compare(final BookInfo o1, final BookInfo o2) {
            if (o1 == o2) {
                return 0;
            }
            int result = 0;
            result = compareSequences(o1, o2);
            if (result == 0) {
                result = compareSeqNo(o1, o2);
            }
            if (result == 0) {
                result = compareBookNames(o1, o2);
            }
            if (result == 0) {
                result = compareAuthors(o1, o2);
            }
            return fixCompareResult(result);
        }
    };

    static class BaseBookInfoComparator implements Comparator<BookInfo> {

        @Override
        public int compare(final BookInfo o1, final BookInfo o2) {
            if (o1 == o2) {
                return 0;
            }
            int result = 0;

            result = compareAuthors(o1, o2);

            if (result == 0) {
                result = compareSequences(o1, o2);
            }
            if (result == 0) {
                result = compareSeqNo(o1, o2);
            }
            if (result == 0) {
                result = compareBookNames(o1, o2);
            }

            return fixCompareResult(result);
        }

        protected int compareAuthors(final BookInfo o1, final BookInfo o2) {
            return o1.getAuthor().compareTo(o2.getAuthor());
        }

        protected int compareSequences(final BookInfo o1, final BookInfo o2) {
            final String s1 = LengthUtils.safeString(o1.getSequence());
            final String s2 = LengthUtils.safeString(o2.getSequence());
            return s1.compareToIgnoreCase(s2);
        }

        protected int compareSeqNo(final BookInfo o1, final BookInfo o2) {
            final Integer no1 = o1.getIntSequenceNo();
            final Integer no2 = o2.getIntSequenceNo();

            if (no1 == null && no2 == null) {
                return 0;
            }

            if (no1 != null && no2 != null) {
                return no1.compareTo(no2);
            }

            if (no1 != null) {
                return 1;
            }

            /* if (no2 != null) */
            return -1;
        }

        protected int compareBookNames(final BookInfo o1, final BookInfo o2) {
            final String n1 = LengthUtils.safeString(o1.getBookName());
            final String n2 = LengthUtils.safeString(o2.getBookName());
            return n1.compareToIgnoreCase(n2);
        }

        protected int fixCompareResult(final int result) {
            return result < 0 ? -1 : result > 0 ? 1 : 0;
        }
    }
}
