/**
 * File: OffsetFilter.java
 * Abstract: TODO add abstract for test.editors.document.v2.tree.OffsetFilter.java
 *
 * @author: Whippet
 * @date: 06.08.2007 21:24:14
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2.tree;

import test.editors.document.v2.IFb2Node;

public class OffsetFilter implements IFb2NodeVisitor {

    private final IFb2NodeVisitor fieldVisitor;

    private final int fieldOffset;

    public OffsetFilter(final int offset, final IFb2NodeVisitor visitor) {
        fieldOffset = offset;
        fieldVisitor = visitor;
    }

    @Override
    public Result handle(final IFb2Node node) {
        if (fieldVisitor == null) {
            throw new IllegalArgumentException("No visitor delegate");
        }

        int nodeFirst = node.getOffset();
		if (fieldOffset < nodeFirst) {
            return Result.Stop;
        }
        int nodeLast = nodeFirst + node.getLength();
		if (nodeFirst <= fieldOffset && fieldOffset < nodeLast) {
            final Result result = fieldVisitor.handle(node);
            return Fb2TreeWalker.min(Result.ProcessOnlyChildren, result);
        }

        return Result.NextSibling;
    }
}