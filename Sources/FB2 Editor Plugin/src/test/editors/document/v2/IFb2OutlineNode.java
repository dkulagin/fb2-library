package test.editors.document.v2;

import java.util.List;

public interface IFb2OutlineNode extends IFb2Node {

    String getTitle();

    List<IFb2OutlineNode> getInner();

}
