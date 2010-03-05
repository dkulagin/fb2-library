package test.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;

public class Fb2Style {
	TextAttribute textAttribute;
	Integer lineIndent;
	Boolean lineJustification;
	Integer lineAlignment;
	Color lineBackground;

	public TextAttribute getTextAttribute() {
		return textAttribute;
	}

	public Integer getLineIndent() {
		return lineIndent;
	}

	public Boolean isLineJustified() {
		return lineJustification;
	}

	public Integer getLineAlignment() {
		return lineAlignment;
	}

	public Color getLineBackground() {
		return lineBackground;
	}

	public Fb2Style(TextAttribute textAttribute, Integer lineIndent, Boolean lineJustification, Integer lineAlignment,
			Color lineBackground) {
		this.textAttribute = textAttribute;
		this.lineIndent = lineIndent;
		this.lineJustification = lineJustification;
		this.lineAlignment = lineAlignment;
		this.lineBackground = lineBackground;
	}
}
