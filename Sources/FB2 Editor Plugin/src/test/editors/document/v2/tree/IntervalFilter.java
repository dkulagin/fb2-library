/**
 * File: IntervalFilter.java
 * Abstract: TODO add abstract for test.editors.document.v2.tree.IntervalFilter.java
 *
 * @author: Whippet
 * @date: 06.08.2007 21:24:22
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2.tree;

import test.editors.document.v2.IFb2Node;

public class IntervalFilter implements IFb2NodeVisitor {

    private final IFb2NodeVisitor fieldVisitor;

    private final int fieldFirst;

    private final int fieldLength;

    private final int fieldLast;

    public IntervalFilter(final int offset, final int length, final IFb2NodeVisitor visitor) {
        fieldVisitor = visitor;
        fieldFirst = offset;
        fieldLength = length;
        fieldLast = offset + length;
    }

    @Override
    public Result handle(final IFb2Node node) {
        final int nodeFirst = node.getOffset();
        final int nodeLast = nodeFirst + node.getLength();

        if (nodeLast <= fieldFirst) {
            return Result.NextSibling;
        }
        if (fieldFirst < nodeLast && nodeFirst <= fieldLast) {
            final Result result = handleImpl(node, nodeFirst, nodeLast);
            if (fieldLast < nodeLast) {
                return Fb2TreeWalker.min(Result.ProcessOnlyChildren, result);
            } else {
                return Fb2TreeWalker.min(Result.Continue, result);
            }
        }
        return Result.Stop;
    }

    /**
     * @return the first
     */
    public int getFirst() {
        return fieldFirst;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return fieldLength;
    }

    /**
     * @return the last
     */
    public int getLast() {
        return fieldLast;
    }

    protected Result handleImpl(final IFb2Node node, final int nodeFirst, final int nodeLast) {
        if (fieldVisitor == null) {
            throw new IllegalArgumentException("No visitor delegate");
        }
        return fieldVisitor.handle(node);
    }
}