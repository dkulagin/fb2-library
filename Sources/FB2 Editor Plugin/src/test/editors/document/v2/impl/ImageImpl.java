package test.editors.document.v2.impl;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Image;
import test.editors.document.v2.IFb2LineNode;

@XmlTag(name = "image", parents = { "section", "body" })
public class ImageImpl extends AbstractNode implements IFb2Image {

	private int fieldLineNumber;
	private String fieldImageName;

	public ImageImpl(final IFb2CompositeNode parent, final Node xmlNode) {
		super(parent, xmlNode);
		if (xmlNode != null) {
			Node namedItem = xmlNode.getAttributes().getNamedItem("l:href");
			if (namedItem != null) {
				String imageName = namedItem.getNodeValue();
				fieldImageName = imageName.startsWith("#") ? imageName.substring(1) : imageName;
			}
		}

	}

	@Override
	protected int calculateLength() {
		return 2;
	}

	@Override
	public String getType() {
		return FB2_IMAGE_TYPE;
	}

	@Override
	public int getLineNumber() {
		return fieldLineNumber;
	}

	@Override
	public void setLineNumber(final int lineNumber) {
		fieldLineNumber = lineNumber;
	}

	@Override
	public String getText() {
		return "\uFFFC"+TextImpl.EXTRA_END_LINE;
	}

	@Override
	public String getStyleId() {
		return FB2_IMAGE_STYLE;
	}

	@Override
	public IFb2LineNode getLine() {
		return this;
	}

	@Override
	public String getImageId() {
		return fieldImageName;
	}

}
