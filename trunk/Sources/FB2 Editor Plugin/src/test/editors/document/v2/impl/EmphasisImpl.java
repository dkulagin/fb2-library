package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2StyleNode;

@XmlTag(name = "emphasis")
public class EmphasisImpl extends AbstractCompositeNode implements IFb2StyleNode {

    public EmphasisImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public String getStyleId() {
        return FB2_EMPHASIS_STYLE;
    }

}
