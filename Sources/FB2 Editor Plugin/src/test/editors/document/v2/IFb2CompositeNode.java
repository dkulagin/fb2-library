package test.editors.document.v2;

import java.util.List;

public interface IFb2CompositeNode extends IFb2Node {

    IFb2Node getFirstChild();

    IFb2Node getLastChild();

    List<IFb2Node> getChildren();

    int getOffset(IFb2Node child);

	void insertBefore(IFb2Node newChild, IFb2Node oldChild);

	void removeChild(IFb2Node child);

	void appendChild(IFb2Node newChild);

}
