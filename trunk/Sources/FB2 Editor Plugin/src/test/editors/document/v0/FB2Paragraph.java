/**
 * File: FB2Paragraph.java
 * Abstract: TODO add abstract for test.editors.FB2Paragraph.java
 *
 * @author: Whippet
 * @date: 03.08.2007 17:03:32
 *
 * History:
 *    [date] [comment]
 */

package test.editors.document.v0;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Region;
import org.w3c.dom.Node;

@Deprecated
public class FB2Paragraph extends Region {

	private Node fieldNode;

	public Node getNode() {
		return fieldNode;
	}

	public FB2Paragraph(int offset, int length, Node node) {
		super(offset, length);
		this.fieldNode = node;
	}

	private List<FB2Region> fieldRegions = new ArrayList<FB2Region>();

	public List<FB2Region> getRegions() {
		return fieldRegions;
	}

	public String getText() {
		StringBuffer buf = new StringBuffer();
		for (FB2Region text : fieldRegions) {
			buf.append(text.getNode().getNodeValue());
		}
		return buf.toString();
	}

}