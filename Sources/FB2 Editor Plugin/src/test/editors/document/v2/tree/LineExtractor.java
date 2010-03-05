/**
 * File: LineExtractor.java
 * Abstract: TODO add abstract for test.editors.document.v2.LineExtractor.java
 *
 * @author: Whippet
 * @date: 06.08.2007 21:42:32
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2.tree;

import java.util.ArrayList;
import java.util.List;

import test.editors.document.v2.IFb2LineNode;
import test.editors.document.v2.IFb2Node;

public class LineExtractor implements IFb2NodeVisitor {
    List<IFb2LineNode> fieldList = new ArrayList<IFb2LineNode>();

    private final boolean fieldOnlyFirst;

    public LineExtractor(final boolean onlyFirst) {
        fieldOnlyFirst = onlyFirst;
    }

    @Override
    public Result handle(final IFb2Node node) {
        if (node instanceof IFb2LineNode) {
            fieldList.add((IFb2LineNode) node);
            if (fieldOnlyFirst) {
                return Result.Stop;
            }
        }
        return Result.Continue;
    }

    public List<IFb2LineNode> getList() {
        return fieldList;
    }
}