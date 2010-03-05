package test.editors.document.v2.impl;

import java.util.List;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2Body;
import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Epigraph;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2OutlineNode;
import test.editors.document.v2.IFb2Section;
import test.editors.document.v2.IFb2Title;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.TextCollector;

@XmlTag(name = "body")
public final class BodyImpl extends AbstractCompositeNode implements IFb2Body {

    public BodyImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

	@Override
	public IFb2Title getTitleNode() {
		return getFirst(IFb2Title.class);
	}
    
    @Override
    public String getTitle() {
        final IFb2Title title = getTitleNode();
        if (title != null) {
            final TextCollector visitor = new TextCollector();
            final List<IFb2Node> children = title.getChildren();
            for (final IFb2Node fb2Node : children) {
                Fb2TreeWalker.visit(fb2Node, visitor);
            }
            return visitor.getText();
        }

        return getTagName();
    }

    @Override
    public List<IFb2Epigraph> getEpigraphs() {
        return getFiltered(IFb2Epigraph.class);
    }

    @Override
    public List<IFb2Section> getSections() {
        return getFiltered(IFb2Section.class);
    }

    @Override
    public List<IFb2OutlineNode> getInner() {
        return getFiltered(IFb2OutlineNode.class);
    }

}
