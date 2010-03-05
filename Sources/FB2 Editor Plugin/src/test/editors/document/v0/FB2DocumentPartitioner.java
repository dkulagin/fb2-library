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

package test.editors.document.v0;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import test.editors.document.FB2Document;


/**
 * TODO add comment for the class.
 *
 */
@Deprecated
public class FB2DocumentPartitioner implements IDocumentPartitioner {

    public static final String CONTENT_TYPE = "text/fb2";

	private List<FB2Paragraph> fieldParagraphs = new ArrayList<FB2Paragraph>();

	/**
	 * @param document
	 * @see org.eclipse.jface.text.IDocumentPartitioner#connect(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void connect(IDocument document) {
		disconnect();

		try {
			XPath xpath1 = XPathFactory.newInstance().newXPath();
			XPath xpath2 = XPathFactory.newInstance().newXPath();
			int offset = 0;
			Object result = xpath1.evaluate("FictionBook/body//p", ((FB2Document) document).getXml(), XPathConstants.NODESET);
			if (result instanceof NodeList) {
				NodeList paragraphs = (NodeList) result;
				for (int paragraphIndex = 0, paragraphsLength = paragraphs.getLength(); paragraphIndex < paragraphsLength; paragraphIndex++) {
					int paragraphStart = offset;
					Node paragraph = paragraphs.item(paragraphIndex);
					Object inners = xpath2.evaluate("descendant::text()", paragraph, XPathConstants.NODESET);
					List<FB2Region> textRegions = new ArrayList<FB2Region>();
					if (inners instanceof NodeList) {
						NodeList texts = (NodeList) inners;
						for (int textIndex = 0, textsLength = texts.getLength(); textIndex < textsLength; textIndex++) {
							Node textNode = texts.item(textIndex);
							if (textNode instanceof Text) {
								Text text = (Text) textNode;
								int length = text.getLength();
								FB2Region textRegion = new FB2Region(offset, length, text);
								textRegions.add(textRegion);
								offset += length;
							}
						}
					}
					FB2Paragraph pRegion = new FB2Paragraph(paragraphStart, offset - paragraphStart, paragraph);
					for (FB2Region region : textRegions) {
						region.setParagraph(pRegion);
					}
					pRegion.getRegions().addAll(textRegions);
					fieldParagraphs.add(pRegion);
				}
			}

		} catch (XPathExpressionException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 *
	 * @see org.eclipse.jface.text.IDocumentPartitioner#disconnect()
	 */
	@Override
	public void disconnect() {
		for (FB2Paragraph paragraph : fieldParagraphs) {
			paragraph.getRegions().clear();
		}
		fieldParagraphs.clear();
	}

	/**
	 * @param offset
	 * @param length
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#computePartitioning(int, int)
	 */
	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
		List<ITypedRegion> list = new ArrayList<ITypedRegion>();
		for (FB2Paragraph paragraph : fieldParagraphs) {
			List<FB2Region> regions = paragraph.getRegions();
			for (FB2Region region : regions) {
				if (region.getOffset()+region.getLength() > offset &&
						region.getOffset() <= offset+length) {
					list.add(region);
				}
			}
		}
		return list.toArray(new ITypedRegion[list.size()]);
	}

	/**
	 * @param event
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param event
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentChanged(org.eclipse.jface.text.DocumentEvent) {
	 */
	@Override
	public boolean documentChanged(DocumentEvent event) {
		return true;
	}

	/**
	 * @param offset
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getContentType(int)
	 */
	@Override
	public String getContentType(int offset) {
		return CONTENT_TYPE;
	}

	/**
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getLegalContentTypes()
	 */
	@Override
	public String[] getLegalContentTypes() {
		return new String[] {CONTENT_TYPE};
	}

	/**
	 * @param offset
	 * @return
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getPartition(int)
	 */
	@Override
	public ITypedRegion getPartition(int offset) {
		for (FB2Paragraph paragraph : fieldParagraphs) {
			List<FB2Region> regions = paragraph.getRegions();
			for (FB2Region region : regions) {
				if (region.getOffset()+region.getLength() > offset &&
						region.getOffset() <= offset) {
					return region;
				}
			}
		}
		return null;
	}

	public List<FB2Paragraph> getParagraphs() {
		return fieldParagraphs;
	}

}
