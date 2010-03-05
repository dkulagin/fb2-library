package test.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import test.editors.document.v2.IFb2VisibleNode;

public class FB2TagScanner implements ITokenScanner {

    private final FB2Configuration config;

    private ITypedRegion[] tokens;

    private int position;

    public FB2TagScanner(final FB2Configuration config) {
        this.config = config;
    }

    @Override
    public int getTokenLength() {
        return tokens[position].getLength();
    }

    @Override
    public int getTokenOffset() {
        return tokens[position].getOffset();
    }

    @Override
    public IToken nextToken() {
        if (tokens != null) {
            do {
                position++;
                if (position < tokens.length) {
                    if (tokens[position] instanceof IFb2VisibleNode) {
                        return config.getToken((IFb2VisibleNode) (tokens[position]));
                    }
                }
            } while (position < tokens.length);
        }
        return Token.EOF;
    }

    @Override
    public void setRange(final IDocument document, final int offset, final int length) {
        if (document == null) {
            tokens = null;
            return;
        }
        try {
            tokens = document.computePartitioning(offset, length);
            position = -1;
        } catch (final BadLocationException e) {
        }
    }

}
