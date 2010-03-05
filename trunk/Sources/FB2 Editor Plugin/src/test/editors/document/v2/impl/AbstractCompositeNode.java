package test.editors.document.v2.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Node;

public abstract class AbstractCompositeNode extends AbstractNode implements IFb2CompositeNode {

	private final List<IFb2Node> fieldChildren = new ArrayList<IFb2Node>();

	protected AbstractCompositeNode(final IFb2CompositeNode parent, final Node xmlNode) {
		super(parent, xmlNode);
	}

	@Override
	public IFb2Node getFirstChild() {
		return fieldChildren.isEmpty() ? null : fieldChildren.get(0);
	}

	@Override
	public IFb2Node getLastChild() {
		return fieldChildren.isEmpty() ? null : fieldChildren.get(fieldChildren.size() - 1);
	}

	@Override
	public List<IFb2Node> getChildren() {
		return fieldChildren;
	}

	@Override
	public int getOffset(final IFb2Node child) {
		int index = getChildIndex(child);

		int offset = this.getOffset();
		for (int i = 0; i < index; i++) {
			offset += getChildren().get(i).getLength();
		}
		return offset;
	}

	public <T extends IFb2Node> List<T> getFiltered(final Class<T> clazz) {
		final List<IFb2Node> children = getChildren();
		final List<T> filtered = new ArrayList<T>(children.size());
		for (final IFb2Node fb2Node : children) {
			if (clazz.isInstance(fb2Node)) {
				filtered.add(clazz.cast(fb2Node));
			}
		}
		return Collections.unmodifiableList(filtered);
	}

	public <T extends IFb2Node> List<T> getFiltered(final Set<Class<? extends T>> classes) {
		final List<IFb2Node> children = getChildren();
		final List<T> filtered = new ArrayList<T>(children.size());
		for (final IFb2Node fb2Node : children) {
			for (final Class<? extends T> clazz : classes) {
				if (clazz.isInstance(fb2Node)) {
					filtered.add(clazz.cast(fb2Node));
					break;
				}
			}
		}
		return Collections.unmodifiableList(filtered);
	}

	public <T extends IFb2Node> T getFirst(final Class<T> clazz) {
		final List<IFb2Node> children = getChildren();
		for (final IFb2Node fb2Node : children) {
			if (clazz.isInstance(fb2Node)) {
				return clazz.cast(fb2Node);
			}
		}
		return null;
	}

	@Override
	protected int calculateLength() {
		int length = 0;
		try {
			final List<IFb2Node> children = getChildren();
			for (final IFb2Node fb2Node : children) {
				length += fb2Node.getLength();
			}
		} catch (final Throwable th) {
			th.printStackTrace();
		}
		return length;
	}


	@Override
	public void insertBefore(IFb2Node newChild, IFb2Node oldChild) {
		if (newChild == null) {
			throw new IllegalArgumentException("New child is null");
		}
		if (oldChild == null) {
			appendChild(newChild);
			return;
		}
		List<IFb2Node> children = getChildren();
		int indexOfOldChild = getChildIndex(oldChild);

		IFb2CompositeNode newChildParent = newChild.getParent();
		if (newChildParent != null) {
			newChildParent.removeChild(newChild);
		}

		newChild.setParent(this);
		children.add(indexOfOldChild, newChild);
		Node newNode = newChild.getXmlNode();
		Node oldNode = oldChild.getXmlNode();
		this.getXmlNode().insertBefore(newNode, oldNode);

	}

	@Override
	public void appendChild(IFb2Node newChild) {
		if (newChild == null) {
			throw new IllegalArgumentException("New child is null");
		}
		IFb2CompositeNode newChildParent = newChild.getParent();
		if (newChildParent != null) {
			newChildParent.removeChild(newChild);
		}

		newChild.setParent(this);
		getChildren().add(newChild);
		Node newNode = newChild.getXmlNode();
		this.getXmlNode().appendChild(newNode);
	}

	@Override
	public void removeChild(IFb2Node child) {
		getChildIndex(child);
		getChildren().remove(child);
		getXmlNode().removeChild(child.getXmlNode());

	}

	private int getChildIndex(IFb2Node child) {
		if (child.getParent() != this) {
			throw new IllegalArgumentException("Not owned node:"+child.getXmlNode());
		}

		final int index = getChildren().indexOf(child);
		if (index == -1) {
			throw new IllegalArgumentException("Not listed node:"+child.getXmlNode());
		}
		return index;
	}

	@Override
	public void clearOffsets() {
		super.clearOffsets();
		List<IFb2Node> children = getChildren();
		for (IFb2Node fb2Node : children) {
			fb2Node.clearOffsets();
		}
	}

}
