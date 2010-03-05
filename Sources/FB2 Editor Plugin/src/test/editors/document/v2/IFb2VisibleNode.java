package test.editors.document.v2;

import org.eclipse.jface.text.ITypedRegion;

public interface IFb2VisibleNode extends IFb2Node, ITypedRegion {

    String FB2_EMPTY_LINE_TYPE = "fb2/emptyLine";

    String FB2_TEXT_TYPE = "fb2/text";

    String FB2_IMAGE_TYPE = "fb2/image";

    IFb2LineNode getLine();
}
