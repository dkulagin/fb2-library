package test.editors.document.v2;

import java.util.List;

public interface IFb2Body extends IFb2CompositeNode, IFb2OutlineNode, IFb2TitledNode {

    List<IFb2Epigraph> getEpigraphs();

    List<IFb2Section> getSections();
}
