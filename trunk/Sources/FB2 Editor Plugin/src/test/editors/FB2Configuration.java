package test.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2StyleNode;
import test.editors.document.v2.IFb2VisibleNode;

public class FB2Configuration extends SourceViewerConfiguration {

	private final ColorManager colorManager;

	private ITextDoubleClickStrategy doubleClickStrategy;

	private ITokenScanner tagScanner;

	private Map<String, Fb2Style> fieldTokens;

	private final TextAttribute fieldDefaultAttrs;

	private final IToken fieldDefaultToken;

	public FB2Configuration(final ColorManager colorManager) {
		this.colorManager = colorManager;
		fieldDefaultAttrs = new TextAttribute(null);
		fieldDefaultToken = new Token(fieldDefaultAttrs);
	}

	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return new String[] { IFb2VisibleNode.FB2_EMPTY_LINE_TYPE, IFb2VisibleNode.FB2_IMAGE_TYPE,
				IFb2VisibleNode.FB2_TEXT_TYPE };
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(final ISourceViewer sourceViewer, final String contentType) {
		if (doubleClickStrategy == null) {
			doubleClickStrategy = new DefaultTextDoubleClickStrategy();
		}
		return doubleClickStrategy;
	}

	public ITokenScanner getScanner() {
		if (tagScanner == null) {
			tagScanner = new FB2TagScanner(this);
		}
		return tagScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();

		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getScanner());
		final String[] types = getConfiguredContentTypes(null);
		for (final String type : types) {
			reconciler.setDamager(dr, type);
			reconciler.setRepairer(dr, type);
		}

		return reconciler;
	}

	public IToken getToken(final IFb2VisibleNode region) {
		IToken result = fieldDefaultToken;
		if (region != null) {
			final MutableTextAttribute finalAttrs = new MutableTextAttribute(fieldDefaultAttrs);
			for (IFb2Node node = region; node != null; node = node.getParent()) {
				if (node instanceof IFb2StyleNode) {
					final String styleId = ((IFb2StyleNode) node).getStyleId();
					final Fb2Style style = getStyles().get(styleId);
					if (style != null) {
						final TextAttribute attr = style.getTextAttribute();
						if (attr != null) {
							finalAttrs.append(attr);
						}
					}
				}
			}
			result = new Token(finalAttrs.getAttributes());
		}

		return result;
	}

	/**
	 * @return the tokens
	 */
	public Map<String, Fb2Style> getStyles() {
		if (fieldTokens == null) {
			fieldTokens = new HashMap<String, Fb2Style>();
			fieldTokens.put(IFb2StyleNode.FB2_EMPHASIS_STYLE, new Fb2Style(new TextAttribute(null, null, SWT.ITALIC),
					null, null, null, null));
			fieldTokens.put(IFb2StyleNode.FB2_STRONG_STYLE, new Fb2Style(new TextAttribute(null, null, SWT.BOLD), null,
					null, null, null));
			fieldTokens.put(IFb2StyleNode.FB2_EPIGRAPH_AUTHOR_STYLE, new Fb2Style(new TextAttribute(colorManager
					.getColor(IFB2ColorConstants.EPIGRAPH_AUTHOR), null, SWT.BOLD), null, null, null, null));
			fieldTokens.put(IFb2StyleNode.FB2_EPIGRAPH_STYLE, new Fb2Style(new TextAttribute(colorManager
					.getColor(IFB2ColorConstants.EPIGRAPH), null, SWT.ITALIC), 0, null, SWT.RIGHT, null));
			fieldTokens.put(IFb2StyleNode.FB2_TITLE_STYLE, new Fb2Style(new TextAttribute(colorManager
					.getColor(IFB2ColorConstants.FB2_TITLE_TEXT), null, 0, new Font(null, "Tahoma", 20, SWT.NORMAL)),
					0, null, SWT.CENTER, colorManager.getColor(IFB2ColorConstants.FB2_TITLE_BACKGROUND)));
			fieldTokens.put(IFb2StyleNode.FB2_SUBTITLE_STYLE, new Fb2Style(new TextAttribute(colorManager
					.getColor(IFB2ColorConstants.FB2_TITLE_TEXT), null, 0, new Font(null, "Tahoma", 16, SWT.BOLD)),
					0, null, SWT.CENTER, null));
			fieldTokens.put(IFb2StyleNode.FB2_SECTION_STYLE, new Fb2Style(null,
					50, false, null, null));
		}
		return fieldTokens;
	}

}