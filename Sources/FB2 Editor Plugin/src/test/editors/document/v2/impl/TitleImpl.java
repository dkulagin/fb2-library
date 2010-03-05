package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Title;

@XmlTag(name = "title")
public class TitleImpl extends AbstractCompositeNode implements IFb2Title {

    public TitleImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public String getStyleId() {
        return FB2_TITLE_STYLE;
    }

}
