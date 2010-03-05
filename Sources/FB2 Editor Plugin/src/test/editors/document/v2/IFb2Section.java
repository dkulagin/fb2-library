package test.editors.document.v2;

import java.util.List;

public interface IFb2Section extends IFb2CompositeNode, IFb2OutlineNode, IFb2StyleNode, IFb2TitledNode {

    List<IFb2Epigraph> getEpigraphs();

    List<IFb2LineNode> getContent();

    List<IFb2Section> getSections();

}
