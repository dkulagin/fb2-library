/**
 * File: FB2ContentOutlinePage.java
 * Abstract: TODO add abstract for test.editors.FB2ContentOutlinePage.java
 *
 * @author: Whippet
 * @date: 03.08.2007 15:22:47
 *
 * History:
 *    [date] [comment]
 */

package test.editors;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import test.editors.document.FB2Document;
import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2OutlineNode;

/**
 * TODO add comment for the class.
 */
public class FB2ContentOutlinePage extends ContentOutlinePage {

    private final IDocumentProvider fieldDocumentProvider;

    private final FB2Editor fieldEditor;

    public FB2ContentOutlinePage(final FB2Editor editor) {
        fieldEditor = editor;
        fieldDocumentProvider = editor.getDocumentProvider();

        fieldEditor.addPropertyListener(new IPropertyListener() {
            @Override
            public void propertyChanged(final Object source, final int propId) {
                if (propId == IEditorPart.PROP_INPUT) {
                    setInput(fieldEditor.getEditorInput());
                }
            }
        });
    }

    @Override
    public void createControl(final Composite parent) {

        super.createControl(parent);

        final TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new OutlineContentProvider());
        viewer.setLabelProvider(new OutlineLabelProvider());
        viewer.addSelectionChangedListener(this);

        setInput(fieldEditor.getEditorInput());
    }

    public void setInput(final IEditorInput input) {
        final IDocument document = fieldDocumentProvider.getDocument(input);
        final TreeViewer treeViewer = getTreeViewer();
        if (treeViewer != null) {
            if (document instanceof FB2Document) {
                treeViewer.setInput(((FB2Document) document).getDocument());
            } else {
                treeViewer.setInput(null);
            }
        }
    }

    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if (!selection.isEmpty()) {
            IFb2OutlineNode node = (IFb2OutlineNode) ((ITreeSelection) selection).getFirstElement();
            fieldEditor.setHighlightRange(node.getOffset(), 0, true);
            fieldEditor.setFocus();
        }
        super.selectionChanged(event);
    }

    class OutlineContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        OutlineContentProvider() {
        }

        public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(final Object parent) {
            return getChildren(parent);
        }

        public Object getParent(final Object child) {
            if (child instanceof IFb2OutlineNode) {
                for (IFb2CompositeNode parent = ((IFb2OutlineNode) child).getParent(); parent != null; parent = parent.getParent()) {
                    if (parent instanceof IFb2OutlineNode) {
                        return parent;
                    }
                }
            }
            return null;
        }

        public Object[] getChildren(final Object parent) {
            if (parent instanceof IFb2OutlineNode) {
                final List<IFb2OutlineNode> inner = ((IFb2OutlineNode) parent).getInner();
                final Object[] children = new Object[inner.size()];
                for (int i = 0; i < children.length; i++) {
                    children[i] = inner.get(i);
                }
                return children;
            }
            return new Object[0];
        }

        public boolean hasChildren(final Object parent) {
            if (parent instanceof IFb2OutlineNode) {
                return !((IFb2OutlineNode) parent).getInner().isEmpty();
            }
            return false;
        }
    }

    class OutlineLabelProvider extends LabelProvider {

        OutlineLabelProvider() {
            super();
        }

        @Override
        public String getText(final Object obj) {
            if (obj instanceof IFb2OutlineNode) {
                return ((IFb2OutlineNode) obj).getTitle();
            }
            return obj.toString();
        }

        @Override
        public Image getImage(final Object obj) {
            return null;
        }
    }
}
