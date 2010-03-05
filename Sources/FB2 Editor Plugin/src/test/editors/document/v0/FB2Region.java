/**
 * File: FB2Region.java
 * Abstract: TODO add abstract for test.editors.FB2Region.java
 *
 * @author: Whippet
 * @date: 03.08.2007 17:03:14
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v0;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.w3c.dom.Text;


@Deprecated
public class FB2Region extends Region implements ITypedRegion {

	private Text fieldNode;
	private FB2Paragraph fieldParagraph;

	public Text getNode() {
		return fieldNode;
	}

	public FB2Region(int offset, int length, Text node) {
		super(offset, length);
		this.fieldNode = node;
	}

	@Override
	public String getType() {
		return FB2DocumentPartitioner.CONTENT_TYPE;
	}

	public FB2Paragraph getParagraph() {
		return fieldParagraph;
	}

	public void setParagraph(FB2Paragraph paragraph) {
		this.fieldParagraph = paragraph;
	}

	public String getText() {
		if (fieldNode != null) {
			return fieldNode.getNodeValue();
		}
		return "";
	}
}