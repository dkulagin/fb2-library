/**
 * File: AbstractNode.java
 * Abstract: TODO add abstract for test.editors.document.v2.impl.AbstractNode.java
 *
 * @author: Whippet
 * @date: 06.08.2007 18:26:35
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2.impl;

import java.util.List;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2LineNode;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Section;

/**
 * TODO add comment for the class.
 */
public abstract class AbstractNode implements IFb2Node {

	private final Node fieldXmlNode;

	private IFb2CompositeNode fieldParent;

	private Integer fieldOffset;

	private Integer fieldLength;

	protected AbstractNode(final IFb2CompositeNode parent, final Node xmlNode) {
		fieldXmlNode = xmlNode;
		setParent(parent);
	}

	@Override
	public final Node getXmlNode() {
		return fieldXmlNode;
	}

	@Override
	public final IFb2CompositeNode getParent() {
		return fieldParent;
	}

	@Override
	public final void setParent(final IFb2CompositeNode parent) {
		fieldParent = parent;
	}

	@Override
	public final int getLength() {
		if (fieldLength == null) {
		fieldLength = calculateLength();
		 }
		return fieldLength;
	}

	@Override
	public final int getOffset() {
		 if (fieldOffset == null) {
		fieldOffset = calculateOffset();
		 }
		return fieldOffset;
	}

	@Override
	public boolean contains(final int offset) {
		return getOffset() <= offset && offset < getOffset() + getLength();
	}

	@Override
	public final String toString() {
		final StringBuilder buf = new StringBuilder();

		buf.append(getTagName());
		buf.append("(");
		buf.append(getOffset());
		buf.append(",");
		buf.append(getLength());

		toString(buf);

		buf.append(")");
		return buf.toString();
	}

	protected void toString(final StringBuilder buffer) {
	}

	protected abstract int calculateLength();

	protected final int calculateOffset() {
		return getParent() != null ? getParent().getOffset(this) : 0;
	}

	protected final String getTagName() {
		final XmlTag annotation = this.getClass().getAnnotation(XmlTag.class);
		return annotation != null ? annotation.name() : null;
	}

	public IFb2LineNode getNextLine() {
		
		for(IFb2Node sibling = getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
			if (sibling instanceof IFb2LineNode) {
				return (IFb2LineNode) sibling;
			}
		}
		
		return null;
	}
	
	public IFb2Node getNextSibling() {
		IFb2CompositeNode parent = getParent();
		if (parent != null) {
			IFb2Node node = this;
			List<IFb2Node> children = parent.getChildren();
			int index = children.indexOf(node);
			for (int i = index + 1; i < children.size(); i++) {
				return children.get(i);
			}
		}
		return null;
	}

	@Override
	public IFb2Section getSectionNode() {
		for(IFb2CompositeNode parent = getParent(); parent != null; parent = parent.getParent()) {
			if (parent instanceof IFb2Section) {
				return (IFb2Section) parent;
			}
		}
		return null;
	}

	@Override
	public void clearOffsets() {
		fieldLength = null;
		fieldOffset = null;
	}

	
}
