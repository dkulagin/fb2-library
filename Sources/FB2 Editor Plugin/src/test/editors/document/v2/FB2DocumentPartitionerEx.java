/**
 * File: FB2DocumentPartitioner.java
 * Abstract: TODO add abstract for test.editors.FB2DocumentPartitioner.java
 *
 * @author: Whippet
 * @date: 30.07.2007 18:27:25
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v2;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;

import test.editors.document.FB2Document;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.IFb2NodeVisitor;
import test.editors.document.v2.tree.LineExtractor;

/**
 * TODO add comment for the class.
 */
public class FB2DocumentPartitionerEx implements IDocumentPartitioner {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(FB2DocumentPartitionerEx.class.getName());

	private IFb2Document fieldDocument;

	private List<IFb2LineNode> fieldLines;

	/**
	 * @param document
	 * @see org.eclipse.jface.text.IDocumentPartitioner#connect(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void connect(final IDocument document) {
		disconnect();

		fieldDocument = Fb2DocumentFactory.create(((FB2Document) document).getXml());
	}

	/**
	 * @see org.eclipse.jface.text.IDocumentPartitioner#disconnect()
	 */
	@Override
	public void disconnect() {
		if (fieldLines != null) {
			fieldLines.clear();
			fieldLines = null;
		}
		if (fieldDocument != null) {
			Fb2TreeWalker.visit(fieldDocument, new Cleaner());
			fieldDocument = null;
		}
	}

	@Override
	public ITypedRegion[] computePartitioning(final int offset, final int length) {
		final TypedRegionExtractor visitor = new TypedRegionExtractor(false);
		Fb2TreeWalker.visit(fieldDocument, offset, length, visitor);
		final List<ITypedRegion> list = visitor.getList();
		return list.toArray(new ITypedRegion[list.size()]);
	}

	public ITypedRegion computePartitioning(final int offset) {
		final TypedRegionExtractor visitor = new TypedRegionExtractor(true);
		Fb2TreeWalker.visit(fieldDocument, offset, visitor);
		final List<ITypedRegion> list = visitor.getList();
		ITypedRegion typedRegion = list.isEmpty() ? null : list.get(0);
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2DocumentPartitionerEx.computePartitioning(" + offset + "): " + typedRegion);
		}
		return typedRegion;
	}

	/**
	 * @param event
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentAboutToBeChanged(final DocumentEvent event) {
	}

	/**
	 * @param event
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentChanged(org.eclipse.jface.text.DocumentEvent) {
	 */
	@Override
	public boolean documentChanged(final DocumentEvent event) {
		getDocument().clearOffsets();
		if (fieldLines != null) {
			fieldLines.clear();
			fieldLines = null;
		}
		return true;
	}

	/**
	 * @param offset
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getContentType(int)
	 */
	@Override
	public String getContentType(final int offset) {
		final ContentTypeExtractor visitor = new ContentTypeExtractor();
		Fb2TreeWalker.visit(fieldDocument, offset, visitor);
		return visitor.fieldContentType;
	}

	/**
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getLegalContentTypes()
	 */
	@Override
	public String[] getLegalContentTypes() {
		return new String[] { IFb2VisibleNode.FB2_EMPTY_LINE_TYPE, IFb2VisibleNode.FB2_IMAGE_TYPE,
				IFb2VisibleNode.FB2_TEXT_TYPE };
	}

	/**
	 * @param offset
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getPartition(int)
	 */
	@Override
	public ITypedRegion getPartition(final int offset) {
		final TypedRegionExtractor visitor = new TypedRegionExtractor(true);
		Fb2TreeWalker.visit(fieldDocument, offset, visitor);
		final List<ITypedRegion> list = visitor.getList();
		return list.isEmpty() ? null : list.get(0);
	}

	public List<IFb2LineNode> getLines() {
		if (fieldLines == null) {
			final LineExtractor visitor = new LineExtractor(false);
			Fb2TreeWalker.visit(fieldDocument, visitor);
			fieldLines = visitor.getList();
		}
		return fieldLines;
	}

	private static class ContentTypeExtractor implements IFb2NodeVisitor {
		private String fieldContentType;

		@Override
		public Result handle(final IFb2Node node) {
			if (node instanceof ITypedRegion) {
				fieldContentType = ((ITypedRegion) node).getType();
				return Result.Stop;
			}
			return Result.Continue;
		}

		public String getContentType() {
			return fieldContentType;
		}
	}

	private static class TypedRegionExtractor implements IFb2NodeVisitor {
		private final List<ITypedRegion> fieldList = new ArrayList<ITypedRegion>();

		private final boolean fieldOnlyFirst;

		public TypedRegionExtractor(final boolean onlyFirst) {
			fieldOnlyFirst = onlyFirst;
		}

		@Override
		public Result handle(final IFb2Node node) {
			if (node instanceof ITypedRegion) {
				fieldList.add((ITypedRegion) node);
				if (fieldOnlyFirst) {
					return Result.Stop;
				}
			}
			return Result.Continue;
		}

		public List<ITypedRegion> getList() {
			return fieldList;
		}
	}

	private static class Cleaner implements IFb2NodeVisitor {
		@Override
		public Result handle(final IFb2Node node) {
			final IFb2CompositeNode parent = node.getParent();
			if (parent != null) {
				parent.getChildren().remove(node);
				node.setParent(null);
			}
			return Result.Continue;
		}
	}

	/**
	 * @return the document
	 */
	public IFb2Document getDocument() {
		return fieldDocument;
	}
}
