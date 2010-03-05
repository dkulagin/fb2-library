package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2EpigraphAuthor;

@XmlTag(name = "text-author")
public class EpigraphAuthorImpl extends ParagraphImpl implements IFb2EpigraphAuthor {

    public EpigraphAuthorImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public String getStyleId() {
        return FB2_EPIGRAPH_AUTHOR_STYLE;
    }
}
