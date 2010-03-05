package test.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import test.editors.document.FB2Document;
import test.editors.document.v2.IFb2Body;
import test.editors.document.v2.IFb2Document;
import test.editors.document.v2.IFb2Image;
import test.editors.document.v2.IFb2LineNode;
import test.editors.document.v2.IFb2Node;
import test.editors.document.v2.IFb2Section;
import test.editors.document.v2.IFb2StyleNode;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.IFb2NodeVisitor;

public class FB2Editor extends AbstractTextEditor {

    private final ColorManager colorManager = new ColorManager();

    private final FB2DocumentProvider fieldDocumentProvider = new FB2DocumentProvider();

    private final FB2Configuration fieldConfiguration = new FB2Configuration(colorManager);

    private FB2ContentOutlinePage fOutlinePage;

    /** The ID of this editor as defined in plugin.xml */
    public static final String EDITOR_ID = "test.editors.FB2Editor";

    /** The ID of the editor context menu */
    public static final String EDITOR_CONTEXT = EDITOR_ID + ".context";

    /** The ID of the editor ruler context menu */
    public static final String RULER_CONTEXT = EDITOR_CONTEXT + ".ruler";

    public FB2Editor() {
        super();
        setSourceViewerConfiguration(fieldConfiguration);
        setDocumentProvider(fieldDocumentProvider);
        setEditorContextMenuId(EDITOR_CONTEXT);
        setRulerContextMenuId(RULER_CONTEXT);
    }

    @Override
    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(final Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            if (fOutlinePage == null) {
                fOutlinePage = new FB2ContentOutlinePage(this);
                if (getEditorInput() != null) {
                    fOutlinePage.setInput(getEditorInput());
                }
            }
            return fOutlinePage;
        }
        return super.getAdapter(required);
    }

    @Override
    protected ISourceViewer createSourceViewer(final Composite parent, final IVerticalRuler ruler, final int styles) {
        final ISourceViewer sourceViewer = super.createSourceViewer(parent, ruler, styles);
        final StyledText textWidget = sourceViewer.getTextWidget();
        if (textWidget != null) {
            textWidget.addLineStyleListener(new FB2LineStyleListener());
            textWidget.addPaintObjectListener(new FB2PaintObjectListener());
            textWidget.addLineBackgroundListener(new FB2LineBackgroundListener());
            textWidget.addListener(SWT.Paint, new Fb2PaintSectionListener());
            textWidget.setWordWrap(true);

        }
        return sourceViewer;
    }

    public StyledText getTextWidget() {
        ISourceViewer sourceViewer = getSourceViewer();
        if (sourceViewer != null) {
            return sourceViewer.getTextWidget();
        }
        return null;
    }

    private final class Fb2PaintSectionListener implements Listener {
        public void handleEvent(final Event event) {
            final IEditorInput input = getEditorInput();
            if (input == null) {
                return;
            }
            final FB2Document document = (FB2Document) getDocumentProvider().getDocument(input);
            final StyledText text = (StyledText) event.widget;
            final Rectangle clientArea = text.getClientArea();
            final int start = text.getOffsetAtLocation(new Point(clientArea.x, clientArea.y));
            int end = document.getLength();
            try {
                end = text.getOffsetAtLocation(new Point(clientArea.x, clientArea.y + clientArea.height));
            } catch (Throwable e) {

            }

            Fb2TreeWalker.visit(document.getDocument(), start, end - start + 1, new IFb2NodeVisitor() {
                @Override
                public Result handle(IFb2Node node) {
                    if (node instanceof IFb2Section) {
                        IFb2Section section = (IFb2Section) node;
                        Point topLeft = text.getLocationAtOffset(section.getOffset());
                        event.gc.setLineStyle(SWT.LINE_DASHDOTDOT);
                        event.gc.drawLine(clientArea.x, topLeft.y, clientArea.x + clientArea.width, topLeft.y);

                        return section.getInner().isEmpty() ? Result.NextSibling : Result.Continue;
                    }
                    if (node instanceof IFb2Body || node instanceof IFb2Document) {
                        return Result.Continue;
                    }
                    return Result.NextSibling;
                }
            });
        }
    }

    private final class FB2LineBackgroundListener implements LineBackgroundListener {

        @Override
        public void lineGetBackground(LineBackgroundEvent event) {
            final IEditorInput input = getEditorInput();
            final FB2Document document = (FB2Document) getDocumentProvider().getDocument(input);

            IRegion partition;
            try {
                partition = document.getLineInformationOfOffset(event.lineOffset);
                if (partition instanceof IFb2LineNode) {
                    IFb2LineNode line = (IFb2LineNode) partition;
                    Color background = null;

                    for (IFb2Node node = line; node != null; node = node.getParent()) {
                        if (node instanceof IFb2StyleNode) {
                            final String styleId = ((IFb2StyleNode) node).getStyleId();
                            final Fb2Style style = fieldConfiguration.getStyles().get(styleId);
                            if (style != null) {
                                final Color attr = style.getLineBackground();
                                if (attr != null && background == null) {
                                    background = attr;
                                }
                            }
                        }
                    }

                    event.lineBackground = background;
                }
            } catch (BadLocationException e) {
            }
        }
    }

    private final class FB2PaintObjectListener implements PaintObjectListener {
        public void paintObject(PaintObjectEvent event) {
            GC gc = event.gc;
            StyleRange style = event.style;
            int start = style.start;
            final IEditorInput input = getEditorInput();
            final FB2Document document = (FB2Document) getDocumentProvider().getDocument(input);

            ITypedRegion partition;
            try {
                partition = document.getPartition(start);
                if (partition instanceof IFb2Image) {
                    IFb2Image image = (IFb2Image) partition;
                    String imageId = image.getImageId();
                    Image image2 = document.getDocument().getImage(imageId);
                    if (image2 != null) {
                        int x = event.x + image2.getBounds().width / 2;
                        int y = event.y + event.ascent - style.metrics.ascent;
                        gc.drawImage(image2, x, y);
                        gc.drawRectangle(x - 1, y - 1, image2.getBounds().width + 2, image2.getBounds().height + 2);
                    }
                }
            } catch (BadLocationException e) {
            }

        }
    }

    private final class FB2LineStyleListener implements LineStyleListener {

        @Override
        public void lineGetStyle(final LineStyleEvent event) {

            final ITokenScanner scanner = fieldConfiguration.getScanner();
            final IEditorInput input = getEditorInput();
            final FB2Document document = (FB2Document) getDocumentProvider().getDocument(input);

            if (document == null) {

                return;
            }

            scanner.setRange(document, event.lineOffset, event.lineText.length());
            final List<StyleRange> list = new ArrayList<StyleRange>();
            for (IToken token = scanner.nextToken(); token != Token.EOF; token = scanner.nextToken()) {
                final TextAttribute data = (TextAttribute) token.getData();
                final StyleRange styleRange = new StyleRange(scanner.getTokenOffset(), scanner.getTokenLength(), data.getForeground(), data.getBackground(),
                        data.getStyle());
                styleRange.font = data.getFont();
                list.add(styleRange);
            }
            event.styles = list.toArray(new StyleRange[list.size()]);

            try {
                final IFb2LineNode lineInfo = (IFb2LineNode) document.getLineInformationOfOffset(event.lineOffset);
                if (lineInfo instanceof IFb2Image) {
                    IFb2Image image = (IFb2Image) lineInfo;
                    String imageId = image.getImageId();
                    Image image2 = document.getDocument().getImage(imageId);
                    if (event.styles.length > 0 && image2 != null) {
                        StyleRange style = event.styles[0];
                        Rectangle rect = image2.getBounds();
                        style.metrics = new GlyphMetrics(rect.height, 0, rect.width);
                    }
                    event.alignment = SWT.CENTER;
                } else {
                    Boolean justify = null;
                    Integer alignment = null;
                    Integer indent = null;
                    for (IFb2Node node = lineInfo; node != null; node = node.getParent()) {
                        if (node instanceof IFb2StyleNode) {
                            final String styleId = ((IFb2StyleNode) node).getStyleId();
                            final Fb2Style style = fieldConfiguration.getStyles().get(styleId);
                            if (style != null) {
                                if (justify == null && style.isLineJustified() != null) {
                                    justify = style.isLineJustified();
                                }
                                if (alignment == null && style.getLineAlignment() != null) {
                                    alignment = style.getLineAlignment();
                                }
                                if (indent == null && style.getLineIndent() != null) {
                                    indent = style.getLineIndent();
                                }
                            }
                        }
                    }
                    if (justify != null) {
                        event.justify = justify;
                    }
                    if (alignment != null) {
                        event.alignment = alignment;
                    }
                    if (indent != null) {
                        event.indent = indent;
                    }
                }
            } catch (final BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

}
