package test.editors.document.v1;

import org.eclipse.jface.text.ITypedRegion;
import org.w3c.dom.Node;

@Deprecated
public interface IFB2Region extends ITypedRegion {

    Node getNode();

    IFB2CompositeRegion getParent();

    void setParent(IFB2CompositeRegion parent);

    IFB2Region getPrevious();

    void setPrevious(IFB2Region prev);

    IFB2Region getNext();

    void setNext(IFB2Region next);
}
