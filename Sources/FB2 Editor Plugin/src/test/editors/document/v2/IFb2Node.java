package test.editors.document.v2;

import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Node;

public interface IFb2Node extends IRegion {

    Node getXmlNode();

    IFb2CompositeNode getParent();

    void setParent(IFb2CompositeNode parent);

    boolean contains(int offset);

	IFb2LineNode getNextLine();

	IFb2Node getNextSibling();
	
	IFb2Section getSectionNode();
	
	void clearOffsets();
}
