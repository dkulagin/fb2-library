package test.editors.document.v2.impl;

import java.util.List;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2LineNode;
import test.editors.document.v2.IFb2MutableText;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Text;

@XmlTag(name = "#text", parents = { "text-author", "p", "strong", "emphasis", "subtitle" })
public class TextImpl extends AbstractNode implements IFb2MutableText {

	public static final String EXTRA_END_LINE = "\u00B6";

	public TextImpl(final IFb2CompositeNode parent, final Node xmlNode) {
		super(parent, xmlNode);
	}

	@Override
	protected int calculateLength() {
		return getText().length();
	}

	@Override
	public String getText() {
		String text = getXmlNode().getNodeValue();
		if (isLastTextNodeInLine()) {
			text += EXTRA_END_LINE;
		}
		return text;
	}

	@Override
	public void setText(final String text) {
		getXmlNode().setNodeValue(text.replaceAll(EXTRA_END_LINE, ""));
	}

	@Override
	public String getType() {
		return FB2_TEXT_TYPE;
	}

	@Override
	public String getStyleId() {
		return FB2_TEXT_STYLE;
	}

	@Override
	public IFb2LineNode getLine() {
		for (IFb2CompositeNode parent = getParent(); parent != null; parent = parent.getParent()) {
			if (parent instanceof IFb2LineNode) {
				return (IFb2LineNode) parent;
			}
		}
		return null;
	}

	/**
	 * @param buffer
	 * @see test.editors.document.v2.impl.AbstractNode#toString(java.lang.StringBuilder)
	 */
	@Override
	protected void toString(final StringBuilder buffer) {
		buffer.append(", ");
		buffer.append(getText());
	}

	public boolean isLastTextNodeInLine() {
		IFb2CompositeNode line = null;
		for (IFb2CompositeNode parent = getParent(); parent != null && line == null; parent = parent.getParent()) {
			if (parent instanceof IFb2LineNode) {
				line = parent;
				break;
			}
		}
		if (line == null) {
			return false;
		}

		IFb2Text lastChild = getLastTextChild(line);
		return lastChild == this;
	}

	public IFb2Text getLastTextChild(IFb2CompositeNode line) {
		List<IFb2Node> childNodes = line.getChildren();
		for (int i = childNodes.size() - 1; i >= 0; i--) {
			IFb2Node item = childNodes.get(i);
			if (item instanceof IFb2Text) {
				return (IFb2Text) item;
			}
			if (item instanceof IFb2CompositeNode) {
				IFb2Text inner = getLastTextChild((IFb2CompositeNode) item);
				if (inner != null) {
					return inner;
				}
			}
		}
		return null;
	}

}
