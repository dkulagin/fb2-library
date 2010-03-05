/**
 * File: IntervalTextCollector.java
 * Abstract: TODO add abstract for test.editors.document.IntervalTextCollector.java
 *
 * @author: Whippet
 * @date: 06.08.2007 22:06:27
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2.tree;

import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Text;

public class IntervalTextCollector extends IntervalFilter implements IFb2NodeVisitor {
    private final StringBuilder fieldBuffer = new StringBuilder();

    public IntervalTextCollector(final int offset, final int length) {
        super(offset, length, null);
    }

    @Override
    protected Result handleImpl(final IFb2Node node, final int nodeFirst, final int nodeLast) {
        if (node instanceof IFb2Text) {
            final int first = Math.max(getFirst(), nodeFirst) - nodeFirst;
            final int last = Math.min(getLast(), nodeLast) - nodeFirst;

            fieldBuffer.append(((IFb2Text) node).getText(), first, last);
        }
        return Result.Continue;
    }

    public String getText() {
        return fieldBuffer.toString();
    }
}