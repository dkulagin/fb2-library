/**
 * File: TextCollector.java
 * Abstract: TODO add abstract for test.editors.document.TextCollector.java
 *
 * @author: Whippet
 * @date: 06.08.2007 22:06:07
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2.tree;

import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Text;

public class TextCollector implements IFb2NodeVisitor {
    private final StringBuilder fieldBuffer = new StringBuilder();

    @Override
    public Result handle(final IFb2Node node) {
        if (node instanceof IFb2Text) {
            fieldBuffer.append(((IFb2Text) node).getText());
        }
        return Result.Continue;
    }

    public StringBuilder getBuffer() {
        return fieldBuffer;
    }

    public String getText() {
        return fieldBuffer.toString();
    }
}