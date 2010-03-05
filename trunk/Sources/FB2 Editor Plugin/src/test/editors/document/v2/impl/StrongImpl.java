package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2StyleNode;

@XmlTag(name = "strong")
public class StrongImpl extends AbstractCompositeNode implements IFb2StyleNode {

    public StrongImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public String getStyleId() {
        return FB2_STRONG_STYLE;
    }

}
