package test.editors.document.v2.impl;

import java.util.List;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Epigraph;
import test.editors.document.v2.IFb2LineNode;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2OutlineNode;
import test.editors.document.v2.IFb2Section;
import test.editors.document.v2.IFb2Title;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.TextCollector;

@XmlTag(name = "section")
public class SectionImpl extends AbstractCompositeNode implements IFb2Section {

    public SectionImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
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
    public List<IFb2LineNode> getContent() {
        return getFiltered(IFb2LineNode.class);
    }

    @Override
    public List<IFb2Section> getSections() {
        return getFiltered(IFb2Section.class);
    }

    @Override
    public List<IFb2OutlineNode> getInner() {
        return getFiltered(IFb2OutlineNode.class);
    }

	@Override
	public String getStyleId() {
        return FB2_SECTION_STYLE;
	}

	@Override
	public IFb2Title getTitleNode() {
		return getFirst(IFb2Title.class);
	}
}
