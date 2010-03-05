package test.editors.document.v2;

import java.util.List;

public interface IFb2Epigraph extends IFb2CompositeNode, IFb2StyleNode {

    List<IFb2Node> getContent();

    List<IFb2EpigraphAuthor> getAuthor();
}
