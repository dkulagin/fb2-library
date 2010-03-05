package test.editors.document.v2;

import java.util.List;

import org.eclipse.swt.graphics.Image;

public interface IFb2Document extends IFb2CompositeNode, IFb2OutlineNode {

    List<IFb2Body> getBodies();

    Image getImage(String imageId);
}
