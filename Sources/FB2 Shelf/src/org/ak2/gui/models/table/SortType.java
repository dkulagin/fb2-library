package org.ak2.gui.models.table;

/**
 * This enum defines sort type
 */
public enum SortType {
    /**
     * No sorting
     */
    None,

    /**
     * Ascending sorting
     */
    Ascending,

    /**
     * Descending sorting
     */
    Descending;

    /**
     * Next sort type.
     * 
     * @return the sort type
     */
    public SortType next() {
        switch (this) {
        case None:
            return Ascending;
        case Ascending:
            return Descending;
        default:
            return None;
        }
    }
}