package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2EmptyLine;
import test.editors.document.v2.IFb2LineNode;

@XmlTag(name = "empty-line")
public class EmptyLineImpl extends AbstractNode implements IFb2EmptyLine {

    private int fieldLineNumber;

    public EmptyLineImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    protected int calculateLength() {
        return 1;
    }

    @Override
    public String getType() {
        return FB2_EMPTY_LINE_TYPE;
    }

    @Override
    public int getLineNumber() {
        return fieldLineNumber;
    }

    @Override
    public void setLineNumber(final int lineNumber) {
        fieldLineNumber = lineNumber;
    }

    @Override
    public IFb2LineNode getLine() {
        return this;
    }

}
