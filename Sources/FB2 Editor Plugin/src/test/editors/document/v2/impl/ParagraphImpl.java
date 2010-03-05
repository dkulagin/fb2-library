package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Paragraph;

@XmlTag(name = "p")
public class ParagraphImpl extends AbstractCompositeNode implements IFb2Paragraph {

    private int fieldLineNumber;

    public ParagraphImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public int getLineNumber() {
        return fieldLineNumber;
    }

    @Override
    public void setLineNumber(final int lineNumber) {
        fieldLineNumber = lineNumber;
    }
}
