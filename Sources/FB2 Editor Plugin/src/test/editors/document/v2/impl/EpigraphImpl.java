package test.editors.document.v2.impl;

import java.util.HashSet;
import java.util.List;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2EmptyLine;
import test.editors.document.v2.IFb2Epigraph;
import test.editors.document.v2.IFb2EpigraphAuthor;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Paragraph;

@XmlTag(name = "epigraph")
public class EpigraphImpl extends AbstractCompositeNode implements IFb2Epigraph {

    public EpigraphImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public List<IFb2EpigraphAuthor> getAuthor() {
        return getFiltered(IFb2EpigraphAuthor.class);
    }

    @Override
    public List<IFb2Node> getContent() {
        final HashSet<Class<? extends IFb2Node>> classes = new HashSet<Class<? extends IFb2Node>>();
        classes.add(IFb2EmptyLine.class);
        classes.add(IFb2Paragraph.class);
        return getFiltered(classes);
    }

    @Override
    public String getStyleId() {
        return FB2_EPIGRAPH_STYLE;
    }

}
