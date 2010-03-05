package test.editors.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import test.editors.XmlUtils;
import test.editors.document.v2.FB2DocumentPartitionerEx;
import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Document;
import test.editors.document.v2.IFb2LineNode;
import test.editors.document.v2.IFb2MutableText;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Section;
import test.editors.document.v2.IFb2VisibleNode;
import test.editors.document.v2.impl.ParagraphImpl;
import test.editors.document.v2.impl.SectionImpl;
import test.editors.document.v2.impl.TextImpl;
import test.editors.document.v2.impl.TitleImpl;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.IntervalTextCollector;
import test.editors.document.v2.tree.TextCollector;

public class FB2Document implements IDocument {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(FB2Document.class.getName());

	private Document fieldDoc;

	private final FB2DocumentPartitionerEx fieldPart = new FB2DocumentPartitionerEx();

	private final ListenerList fDocumentListeners = new ListenerList();

	private final ListenerList fPrenotifiedDocumentListeners = new ListenerList();

	private final ListenerList fDocumentPartitioningListeners = new ListenerList();

	private final Map<String, List<Position>> fPositions = new HashMap<String, List<Position>>();

	/** All registered document position updaters */
	private final List<IPositionUpdater> fPositionUpdaters = new ArrayList<IPositionUpdater>();

	private long fNextModificationStamp;

	private long fModificationStamp;

	public FB2Document() {
		super();
	}

	/**
	 * @return the doc
	 */
	public Document getXml() {
		return fieldDoc;
	}

	public void setXml(final Document doc) {
		fieldDoc = doc;
		fieldPart.connect(this);
	}

	public IFb2Document getDocument() {
		return fieldPart.getDocument();
	}

	@Override
	public int computeNumberOfLines(final String text) {
		final String replace = text.replace("\r\n", "\n");
		if ("\n".equals(replace)) {
			return 1;
		}
		int length = 0;
		for (int i = 0; i < replace.length(); i++) {
			if (replace.charAt(i) == '\n') {
				length++;
			}
		}
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("computeNumberOfLines(" + text + ")=" + length);
		}
		return length;
	}

	@Override
	public ITypedRegion[] computePartitioning(final int offset, final int length) throws BadLocationException {
		return fieldPart.computePartitioning(offset, length);
	}

	@Override
	public String get() {
		final TextCollector visitor = new TextCollector();
		Fb2TreeWalker.visit(fieldPart.getDocument(), visitor);
		return visitor.getText();
	}

	@Override
	public String get(final int offset, final int length) throws BadLocationException {
		final IntervalTextCollector visitor = new IntervalTextCollector(offset, length);
		Fb2TreeWalker.visit(fieldPart.getDocument(), visitor);
		final String text = visitor.getText();
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2Document.get(" + offset + ", " + length + "): " + text);
		}
		return text;
	}

	@Override
	public char getChar(final int offset) throws BadLocationException {
		return get(offset, 1).charAt(0);
	}

	@Override
	public String getContentType(final int offset) throws BadLocationException {
		return fieldPart.getContentType(offset);
	}

	@Override
	public IDocumentPartitioner getDocumentPartitioner() {
		return fieldPart;
	}

	@Override
	public String[] getLegalContentTypes() {
		return fieldPart.getLegalContentTypes();
	}

	@Override
	public String[] getLegalLineDelimiters() {
		System.out.println("FB2Document.getLegalLineDelimiters()");
		return new String[] { "\n" };
	}

	@Override
	public int getLength() {
		return fieldPart.getDocument().getLength();
	}

	@Override
	public String getLineDelimiter(final int line) throws BadLocationException {
		System.out.println("FB2Document.getLineDelimiter(" + line + ")");
		int lines = fieldPart.getLines().size();

		if (line < 0 || line > lines)
			throw new BadLocationException();

		if (lines == 0)
			return null;

		if (line == lines)
			return null;
		return "\n";
	}

	@Override
	public IRegion getLineInformation(final int line) throws BadLocationException {
		final IFb2LineNode fb2LineNode = fieldPart.getLines().get(line);
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2Document.getLineInformation(" + line + "): " + fb2LineNode);
		}
		return fb2LineNode;
	}

	@Override
	public IRegion getLineInformationOfOffset(final int offset) throws BadLocationException {
		final int lineOfOffset = getLineOfOffset(offset);
		final IRegion lineInformation = getLineInformation(lineOfOffset);
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2Document.getLineInformationOfOffset(" + offset + "): " + lineInformation);
		}
		return lineInformation;
	}

	@Override
	public int getLineLength(final int line) throws BadLocationException {
		final IFb2LineNode fb2LineNode = fieldPart.getLines().get(line);
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2Document.getLineLength(" + line + "): " + fb2LineNode);
		}
		return fb2LineNode.getLength();
	}

	@Override
	public int getLineOfOffset(final int offset) throws BadLocationException {

		final List<IFb2LineNode> paragraphs = fieldPart.getLines();
		int result = paragraphs.size() - 1;
		for (int i = 0; i < paragraphs.size(); i++) {
			if (paragraphs.get(i).contains(offset)) {
				result = i;
				break;
			}
		}
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2Document.getLineOfOffset(" + offset + "): " + result);
			System.out.println("FB2Document.getLineOfOffset(" + offset + "): " + result);
		}
		return result;
	}

	public IFb2LineNode getLineNodeOfOffset(final int offset) {

		final List<IFb2LineNode> paragraphs = fieldPart.getLines();
		for (int i = 0; i < paragraphs.size(); i++) {
			IFb2LineNode fb2LineNode = paragraphs.get(i);
			if (fb2LineNode.contains(offset)) {
				return fb2LineNode;
			}
		}
		return null;
	}

	@Override
	public int getLineOffset(final int line) throws BadLocationException {
		final IFb2LineNode fb2LineNode = fieldPart.getLines().get(line);
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("FB2Document.getLineOffset(" + line + "): " + fb2LineNode.getOffset());
		}
		return fb2LineNode.getOffset();
	}

	@Override
	public int getNumberOfLines() {
		return fieldPart.getLines().size();
	}

	@Override
	public int getNumberOfLines(final int offset, final int length) throws BadLocationException {
		int lines = 0;
		if (length > 0) {
			final int firstLine = getLineOfOffset(offset);
			final int lastLine = getLineOfOffset(offset + length);
			lines = lastLine - firstLine + 1;
		} else {
			lines = 1;
		}
		if (LOGGER.isLoggable(Level.FINER)) {
			LOGGER.finer("getNumberOfLines(" + offset + "," + length + "): " + (lines));
		}
		return lines;
	}

	@Override
	public void replace(final int offset, final int length, final String text) throws BadLocationException {
		try {

			if (offset >= getLength() - 1) {
				return;
			}
			if (length == 0 && text.length() == 0) {
				return;
			}

			String removed = get(offset, length);

			int removedLength = length;
			if (removed.endsWith(TextImpl.EXTRA_END_LINE)) {
				removedLength++;
			}

			final DocumentEvent event = new DocumentEvent(this, offset, length, text);
			fireDocumentAboutToBeChanged(event);

			final ITypedRegion[] regions = computePartitioning(offset, removedLength);

			final IFb2VisibleNode leftRegion = (IFb2VisibleNode) regions[0];
			final IFb2VisibleNode rightRegion = (IFb2VisibleNode) regions[regions.length - 1];

			final IFb2MutableText leftTextNode = (IFb2MutableText) (leftRegion instanceof IFb2MutableText ? leftRegion
					: null);
			final IFb2MutableText rightTextNode = (IFb2MutableText) (rightRegion instanceof IFb2MutableText ? rightRegion
					: null);

			String leftText = "";
			if (leftRegion != rightRegion) {
				if (rightTextNode != null) {
					// Retrieve unmodified tail of the right node
					final String rightText = rightTextNode.getText().substring(
							offset + length - rightRegion.getOffset());
					// Shrink the right node
					rightTextNode.setText(rightText);
				}
				if (leftTextNode != null) {
					// Merge unmodified head of the left node and inserted text
					leftText = leftTextNode.getText().substring(0, offset - leftRegion.getOffset()) + text;
					// Set the left node
					leftTextNode.setText(leftText);
				}

				IFb2LineNode leftLine = leftRegion.getLine();
				IFb2LineNode rightLine = rightRegion.getLine();

				mergeLines(leftLine, rightLine);

			} else if (leftTextNode != null) {
				StringBuilder buf = new StringBuilder();
				buf.append(leftTextNode.getText().substring(0, offset - leftRegion.getOffset()));
				buf.append(text);
				buf.append(leftTextNode.getText().substring(offset + length - rightRegion.getOffset()));

				leftText = buf.toString();
				leftTextNode.setText(leftText);
			}

			if (leftRegion instanceof IFb2MutableText) {
				fixLeftNode((IFb2MutableText) leftRegion/* , leftText */);
			}

			removeRegions(regions);

			// fieldPart.connect(this);
			if (fieldPart.documentChanged(event)) {
				fireDocumentPartitioningChanged();
			}

			final long modificationStamp = length == 0 && (text == null || text.length() == 0) ? getModificationStamp()
					: getNextModificationStamp();
			fModificationStamp = modificationStamp;
			fNextModificationStamp = Math.max(fModificationStamp, fNextModificationStamp);
			event.fModificationStamp = fModificationStamp;
			fireDocumentChanged(event);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	private void removeRegions(final ITypedRegion[] regions) {
		HashSet<IFb2CompositeNode> nodes = new HashSet<IFb2CompositeNode>();
		for (int i = 1; i < regions.length - 1; i++) {
			if (regions[i] instanceof IFb2VisibleNode) {
				final IFb2VisibleNode region = (IFb2VisibleNode) regions[i];
				region.getXmlNode().getParentNode().removeChild(region.getXmlNode());
				IFb2CompositeNode parent = region.getParent();
				parent.getChildren().remove(region);
				nodes.add(parent);
			}
		}
		checkParents(nodes);
	}

	private void mergeLines(IFb2LineNode leftLine, IFb2LineNode rightLine) {
		if (leftLine == rightLine) {
			return;
		}
		if (leftLine instanceof IFb2CompositeNode && rightLine instanceof IFb2CompositeNode) {
			IFb2CompositeNode leftParent = (IFb2CompositeNode) leftLine;
			IFb2CompositeNode rightParent = (IFb2CompositeNode) rightLine;

			List<IFb2Node> rightChildren = rightParent.getChildren();
			for (IFb2Node child : rightChildren) {
				IFb2Node lastChild = leftParent.getLastChild();
				if (lastChild instanceof IFb2MutableText && child instanceof IFb2MutableText) {
					((IFb2MutableText) lastChild).setText(((IFb2MutableText) lastChild).getText()
							+ ((IFb2MutableText) child).getText().replaceAll(TextImpl.EXTRA_END_LINE, ""));
				} else {
					leftParent.getXmlNode().appendChild(child.getXmlNode());
					leftParent.getChildren().add(child);
					child.setParent(leftParent);
				}
			}

			IFb2CompositeNode parent = rightParent.getParent();
			if (parent != null) {
				parent.getChildren().remove(rightParent);
				parent.getXmlNode().removeChild(rightParent.getXmlNode());

				HashSet<IFb2CompositeNode> nodes = new HashSet<IFb2CompositeNode>();
				nodes.add(parent);
				checkParents(nodes);
			}
		}
	}

	private void checkParents(HashSet<IFb2CompositeNode> nodes) {
		while (!nodes.isEmpty()) {
			IFb2CompositeNode[] nodesToCheck = nodes.toArray(new IFb2CompositeNode[nodes.size()]);
			for (int i = 0; i < nodesToCheck.length; i++) {
				if (nodesToCheck[i].getChildren().isEmpty()) {
					IFb2CompositeNode parent = nodesToCheck[i].getParent();
					if (parent != null) {
						parent.getXmlNode().removeChild(nodesToCheck[i].getXmlNode());
						parent.getChildren().remove(nodesToCheck[i]);
						nodes.add(parent);
					}
				}
				nodes.remove(nodesToCheck[i]);
			}
		}
	}

	public void fixLeftNode(final IFb2MutableText leftRegion/* , final String leftText */) {
		String leftText = leftRegion.getText();
		if (leftText == null || leftText.length() == 0) {
			return;
		}
		final IFb2LineNode paragraph = leftRegion.getLine();

		final IFb2CompositeNode parent = paragraph != null ? paragraph.getParent() : null;
		if (parent == null) {
			return;
		}

		String[] fragments = leftText.split(XmlUtils.EOL);
		if (leftText.endsWith(XmlUtils.EOL)) {
			fragments = Arrays.copyOf(fragments, fragments.length + 1);
			fragments[fragments.length - 1] = "";
		}
		if (fragments == null || fragments.length == 1) {
			return;
		}

		leftRegion.setText(fragments[0]);
		final IFb2LineNode nextLine = paragraph.getNextLine();

		if (nextLine instanceof IFb2CompositeNode) {
			IFb2CompositeNode nextCompositeLine = (IFb2CompositeNode) nextLine;

			final IFb2Node refChild = nextCompositeLine.getFirstChild();
			for (IFb2Node parent1 = leftRegion; parent1 != paragraph; parent1 = parent1.getParent()) {
				for (IFb2Node movedNode = parent1.getNextSibling(); movedNode != null; movedNode = parent1
						.getNextSibling()) {
					nextCompositeLine.insertBefore(movedNode, refChild);
				}
			}
		}

		for (int index = 1; index < fragments.length; index++) {
			final Node newPNode = getXml().createElement("p");
			final IFb2CompositeNode newParagraph = new ParagraphImpl(null, newPNode);
			parent.insertBefore(newParagraph, nextLine);
			final Text newTextNode = getXml().createTextNode("");
			final IFb2MutableText newText = new TextImpl(null, newTextNode);
			newText.setText(fragments[index]);
			newParagraph.appendChild(newText);
		}
	}

	@Override
	public int search(final int startOffset, final String findString, final boolean forwardSearch,
			final boolean caseSensitive, final boolean wholeWord) throws BadLocationException {
		throw new RuntimeException("search Not implemented!!!");
	}

	@Override
	public void set(final String text) {
		throw new RuntimeException("set Not implemented!!!");
	}

	@Override
	public void setDocumentPartitioner(final IDocumentPartitioner partitioner) {
	}

	@Override
	public void addDocumentListener(final IDocumentListener listener) {
		fDocumentListeners.add(listener);
	}

	@Override
	public void addDocumentPartitioningListener(final IDocumentPartitioningListener listener) {
		Assert.isNotNull(listener);
		fDocumentPartitioningListeners.add(listener);
	}

	@Override
	public void addPosition(final Position position) throws BadLocationException {
		try {
			addPosition(DEFAULT_CATEGORY, position);
		} catch (final BadPositionCategoryException e) {
		}
	}

	@Override
	public void addPosition(final String category, final Position position) throws BadLocationException,
			BadPositionCategoryException {

		if ((0 > position.offset) || (0 > position.length) || (position.offset + position.length > getLength())) {
			throw new BadLocationException();
		}

		if (category == null) {
			throw new BadPositionCategoryException();
		}

		final List<Position> list = fPositions.get(category);
		if (list == null) {
			throw new BadPositionCategoryException();
		}

		list.add(computeIndexInPositionList(list, position.offset), position);
	}

	protected int computeIndexInPositionList(final List<Position> positions, final int offset) {

		if (positions.size() == 0) {
			return 0;
		}

		int left = 0;
		int right = positions.size() - 1;
		int mid = 0;
		Position p = null;

		while (left < right) {

			mid = (left + right) / 2;

			p = positions.get(mid);
			if (offset < p.getOffset()) {
				if (left == mid) {
					right = left;
				} else {
					right = mid - 1;
				}
			} else if (offset > p.getOffset()) {
				if (right == mid) {
					left = right;
				} else {
					left = mid + 1;
				}
			} else if (offset == p.getOffset()) {
				left = right = mid;
			}

		}

		int pos = left;
		p = positions.get(pos);
		if (offset > p.getOffset()) {
			// append to the end
			pos++;
		} else {
			// entry will became the first of all entries with the same offset
			do {
				--pos;
				if (pos < 0) {
					break;
				}
				p = positions.get(pos);
			} while (offset == p.getOffset());
			++pos;
		}

		Assert.isTrue(0 <= pos && pos <= positions.size());

		return pos;
	}

	@Override
	public void addPositionCategory(final String category) {
		if (category == null) {
			return;
		}

		if (!containsPositionCategory(category)) {
			fPositions.put(category, new ArrayList<Position>());
		}
	}

	@Override
	public void addPositionUpdater(final IPositionUpdater updater) {
		insertPositionUpdater(updater, fPositionUpdaters.size());
	}

	@Override
	public void addPrenotifiedDocumentListener(final IDocumentListener documentAdapter) {
		Assert.isNotNull(documentAdapter);
		fPrenotifiedDocumentListeners.add(documentAdapter);
	}

	@Override
	public int computeIndexInCategory(final String category, final int offset) throws BadLocationException,
			BadPositionCategoryException {
		if (0 > offset || offset > getLength()) {
			throw new BadLocationException();
		}

		final List<Position> c = fPositions.get(category);
		if (c == null) {
			throw new BadPositionCategoryException();
		}

		return computeIndexInPositionList(c, offset);
	}

	@Override
	public boolean containsPosition(final String category, final int offset, final int length) {
		if (category == null) {
			return false;
		}

		final List<Position> list = fPositions.get(category);
		if (list == null) {
			return false;
		}

		final int size = list.size();
		if (size == 0) {
			return false;
		}

		int index = computeIndexInPositionList(list, offset);
		if (index < size) {
			Position p = list.get(index);
			while (p != null && p.offset == offset) {
				if (p.length == length) {
					return true;
				}
				++index;
				p = (index < size) ? (Position) list.get(index) : null;
			}
		}

		return false;
	}

	@Override
	public boolean containsPositionCategory(final String category) {
		if (category != null) {
			return fPositions.containsKey(category);
		}
		return false;
	}

	@Override
	public ITypedRegion getPartition(final int offset) throws BadLocationException {
		return fieldPart.computePartitioning(offset);
	}

	@Override
	public String[] getPositionCategories() {
		final String[] categories = new String[fPositions.size()];
		final Iterator<String> keys = fPositions.keySet().iterator();
		for (int i = 0; i < categories.length; i++) {
			categories[i] = keys.next();
		}
		return categories;
	}

	@Override
	public IPositionUpdater[] getPositionUpdaters() {
		return null;
	}

	@Override
	public Position[] getPositions(final String category) throws BadPositionCategoryException {
		if (category == null) {
			throw new BadPositionCategoryException();
		}

		final List<Position> c = fPositions.get(category);
		if (c == null) {
			throw new BadPositionCategoryException();
		}

		final Position[] positions = new Position[c.size()];
		c.toArray(positions);
		return positions;
	}

	@Override
	public void insertPositionUpdater(final IPositionUpdater updater, final int index) {
		for (int i = fPositionUpdaters.size() - 1; i >= 0; i--) {
			if (fPositionUpdaters.get(i) == updater) {
				return;
			}
		}

		if (index == fPositionUpdaters.size()) {
			fPositionUpdaters.add(updater);
		} else {
			fPositionUpdaters.add(index, updater);
		}
	}

	@Override
	public void removeDocumentListener(final IDocumentListener listener) {
		fDocumentListeners.remove(listener);
	}

	@Override
	public void removeDocumentPartitioningListener(final IDocumentPartitioningListener listener) {
		Assert.isNotNull(listener);
		fDocumentPartitioningListeners.remove(listener);
	}

	@Override
	public void removePosition(final Position position) {
		try {
			removePosition(DEFAULT_CATEGORY, position);
		} catch (final BadPositionCategoryException e) {
		}
	}

	@Override
	public void removePosition(final String category, final Position position) throws BadPositionCategoryException {
		if (position == null) {
			return;
		}

		if (category == null) {
			throw new BadPositionCategoryException();
		}

		final List<Position> c = fPositions.get(category);
		if (c == null) {
			throw new BadPositionCategoryException();
		}

		// remove based on identity not equality
		final int size = c.size();
		for (int i = 0; i < size; i++) {
			if (position == c.get(i)) {
				c.remove(i);
				return;
			}
		}
	}

	@Override
	public void removePositionCategory(final String category) throws BadPositionCategoryException {
		if (category == null) {
			return;
		}

		if (!containsPositionCategory(category)) {
			throw new BadPositionCategoryException();
		}

		fPositions.remove(category);
	}

	@Override
	public void removePositionUpdater(final IPositionUpdater updater) {
	}

	@Override
	public void removePrenotifiedDocumentListener(final IDocumentListener documentAdapter) {
		Assert.isNotNull(documentAdapter);
		fPrenotifiedDocumentListeners.remove(documentAdapter);
	}

	/**
	 * Fires the given document event to all registers document listeners informing them about the forthcoming document
	 * manipulation. Uses a robust iterator.
	 * 
	 * @param event
	 *            the event to be sent out
	 */
	protected void fireDocumentAboutToBeChanged(final DocumentEvent event) {

		fieldPart.documentAboutToBeChanged(event);

		Object[] listeners = fPrenotifiedDocumentListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IDocumentListener) listeners[i]).documentAboutToBeChanged(event);
		}

		listeners = fDocumentListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IDocumentListener) listeners[i]).documentAboutToBeChanged(event);
		}
	}

	/**
	 * Updates the internal document structures and informs all document listeners if listener notification has been
	 * enabled. Otherwise it remembers the event to be sent to the listeners on resume.
	 * 
	 * @param event
	 *            the document event to be sent out
	 */
	protected void fireDocumentChanged(final DocumentEvent event) {
		updateDocumentStructures(event);

		doFireDocumentChanged(event);
	}

	/**
	 * Updates document partitioning and document positions according to the specification given by the document event.
	 * 
	 * @param event
	 *            the document event describing the change to which structures must be adapted
	 */
	protected void updateDocumentStructures(final DocumentEvent event) {
		if (fPositions.size() > 0) {
			updatePositions(event);
		}
	}

	/**
	 * Notifies all listeners about the given document change. Uses a robust iterator.
	 * <p>
	 * Executes all registered post notification replace operation.
	 * 
	 * @param event
	 *            the event to be sent out.
	 */
	protected void doFireDocumentChanged(final DocumentEvent event) {
		Object[] listeners = fPrenotifiedDocumentListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IDocumentListener) listeners[i]).documentChanged(event);
		}

		listeners = fDocumentListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IDocumentListener) listeners[i]).documentChanged(event);
		}
	}

	/**
	 * Updates all positions of all categories to the change described by the document event. All registered document
	 * updaters are called in the sequence they have been arranged. Uses a robust iterator.
	 * 
	 * @param event
	 *            the document event describing the change to which to adapt the positions
	 */
	protected void updatePositions(final DocumentEvent event) {
		final List<IPositionUpdater> list = new ArrayList<IPositionUpdater>(fPositionUpdaters);
		final Iterator<IPositionUpdater> e = list.iterator();
		while (e.hasNext()) {
			final IPositionUpdater u = e.next();
			u.update(event);
		}
	}

	private long getNextModificationStamp() {
		if (fNextModificationStamp == Long.MAX_VALUE
				|| fNextModificationStamp == IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP) {
			fNextModificationStamp = 0;
		} else {
			fNextModificationStamp = fNextModificationStamp + 1;
		}

		return fNextModificationStamp;
	}

	private long getModificationStamp() {
		return fModificationStamp;
	}

	public String getXmlString() {
		try {
			final ByteArrayOutputStream output = new ByteArrayOutputStream();

			try {
				output.write("<?xml version=\"1.0\" encoding=\"windows-1251\" standalone=\"no\"?>".getBytes());
			} catch (final IOException e) {
			}
			final DOMSource fb2FileSource = new DOMSource(fieldDoc);
			final StreamResult htmlFile = new StreamResult(output);

			final Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "windows-1251");
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");

			t.transform(fb2FileSource, htmlFile);
			return output.toString();
		} catch (final Exception e) {
			LOGGER.severe("exception: " + e);
		}
		return "";
	}

	/**
	 * Fires the document partitioning changed notification to all registered document partitioning listeners. Uses a
	 * robust iterator.
	 * 
	 */
	protected void fireDocumentPartitioningChanged() {
		if (fDocumentPartitioningListeners == null)
			return;

		Object[] listeners = fDocumentPartitioningListeners.getListeners();
		for (int i = 0; i < listeners.length; i++)
			((IDocumentPartitioningListener) listeners[i]).documentPartitioningChanged(this);
	}

	public void splitSectionAtOffset(int offset) {
		IFb2LineNode lineNodeOfOffset = getLineNodeOfOffset(offset);
		if (lineNodeOfOffset != null) {
			final DocumentEvent event = new DocumentEvent(this, offset, 0, "");
			fireDocumentAboutToBeChanged(event);
			IFb2Section section = lineNodeOfOffset.getSectionNode();
			IFb2CompositeNode parent = section.getParent();
			IFb2Node nextSection = section.getNextSibling();

			final Node newSectionNode = getXml().createElement("section");
			final IFb2CompositeNode newSection = new SectionImpl(null, newSectionNode);
			parent.insertBefore(newSection, nextSection);
			List<IFb2Node> nodesToMove = new ArrayList<IFb2Node>();
			for (IFb2Node sibling = lineNodeOfOffset.getNextSibling(); sibling != null; sibling = sibling
					.getNextSibling()) {
				nodesToMove.add(sibling);
			}

			for (IFb2Node fb2Node : nodesToMove) {
				newSection.appendChild(fb2Node);
			}

			if (fieldPart.documentChanged(null)) {
				fireDocumentPartitioningChanged();
			}

			final long modificationStamp = getNextModificationStamp();
			fModificationStamp = modificationStamp;
			fNextModificationStamp = Math.max(fModificationStamp, fNextModificationStamp);
			event.fModificationStamp = fModificationStamp;
			fireDocumentChanged(event);
		}
	}

	public IRegion addTitleAtOffset(int offset) {
		IFb2LineNode lineNodeOfOffset = getLineNodeOfOffset(offset);
		if (lineNodeOfOffset != null) {
			IFb2Section section = lineNodeOfOffset.getSectionNode();
			if ((section != null)&& (section.getTitleNode() == null)) {
				final DocumentEvent event = new DocumentEvent(this, section.getOffset(), 0, "TITLE\n");
				fireDocumentAboutToBeChanged(event);

				final Node newTitleNode = getXml().createElement("title");
				final IFb2CompositeNode newTitle = new TitleImpl(null, newTitleNode);
				section.insertBefore(newTitle, section.getFirstChild());

				final Node newPNode = getXml().createElement("p");
				final IFb2CompositeNode newParagraph = new ParagraphImpl(null, newPNode);
				newTitle.appendChild(newParagraph);
				
				final Text newTextNode = getXml().createTextNode("TITLE");
				final IFb2MutableText newText = new TextImpl(null, newTextNode);
				newParagraph.appendChild(newText);
				
				
				if (fieldPart.documentChanged(null)) {
					fireDocumentPartitioningChanged();
				}

				final long modificationStamp = getNextModificationStamp();
				fModificationStamp = modificationStamp;
				fNextModificationStamp = Math.max(fModificationStamp, fNextModificationStamp);
				event.fModificationStamp = fModificationStamp;
				fireDocumentChanged(event);
				return newTitle;
			}
		}
		return null;
	}

}
