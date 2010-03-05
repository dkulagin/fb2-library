package test.editors.document.v2.impl;

import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2OutlineNode;
import test.editors.document.v2.IFb2Subtitle;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.TextCollector;

@XmlTag(name = "subtitle")
public class SubtitleImpl extends ParagraphImpl implements IFb2Subtitle {

    public SubtitleImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(parent, xmlNode);
    }

    @Override
    public String getTitle() {
        final TextCollector visitor = new TextCollector();
        Fb2TreeWalker.visit(this, visitor);
        String text = visitor.getText();
        return text.length() > 0 ? text : getTagName();
    }

    @Override
    public String getStyleId() {
        return FB2_SUBTITLE_STYLE;
    }

    @Override
    public List<IFb2OutlineNode> getInner() {
        return Collections.emptyList();
    }
}
