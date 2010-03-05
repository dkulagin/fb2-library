package test.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.w3c.dom.Document;

import test.editors.document.FB2Document;

public class FB2DocumentProvider extends FileDocumentProvider {

    /**
     * @return
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
     */
    @Override
    protected IDocument createEmptyDocument() {
        return new FB2Document();
    }

    @Override
    public IAnnotationModel getAnnotationModel(final Object element) {
        return null;
    }

    @Override
    protected void doResetDocument(Object element, IProgressMonitor monitor) throws CoreException {
        super.doResetDocument(element, monitor);
    }

    @Override
    protected void refreshFile(IFile file, IProgressMonitor monitor) throws CoreException {
        super.refreshFile(file, monitor);
    }

    @Override
    protected void refreshFile(IFile file) throws CoreException {
        super.refreshFile(file);
    }

    @Override
    protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {

        InputStream contentStream = null;
        try {
            if (editorInput instanceof IFileEditorInput) {
                IFile file = ((IFileEditorInput) editorInput).getFile();
                contentStream = file.getContents(false);
            } else if (editorInput instanceof FileStoreEditorInput) {
                URI uri = ((FileStoreEditorInput) editorInput).getURI();
                contentStream = uri.toURL().openConnection().getInputStream();
            }
            if (contentStream == null) {
                return false;
            }
            setDocumentContent(document, contentStream, encoding);
            return true;
        } catch (Exception ex) {
            String message = ex.getMessage() == null ? "" : ex.getMessage();
            IStatus s = new Status(4, "org.eclipse.ui", 0, message, ex);
            throw new CoreException(s);
        } finally {
            try {
                contentStream.close();
            } catch (IOException _ex) {
            }
        }
    }

    @Override
    protected boolean setDocumentContent(IDocument document, IEditorInput editorInput) throws CoreException {
        return super.setDocumentContent(document, editorInput);
    }

    /**
     * @param document
     * @param contentStream
     * @param encoding
     * @throws CoreException
     * @see org.eclipse.ui.editors.text.StorageDocumentProvider#setDocumentContent(org.eclipse.jface.text.IDocument, java.io.InputStream, java.lang.String)
     */
    @Override
    protected void setDocumentContent(final IDocument document, final InputStream contentStream, final String encoding) throws CoreException {
        try {
            final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = docBuilder.parse(contentStream);

            if (document instanceof FB2Document) {
                ((FB2Document) document).setXml(doc);
            }

        } catch (final Exception x) {
            final String message = (x.getMessage() != null ? x.getMessage() : ""); //$NON-NLS-1$
            final IStatus s = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, message, x);
            throw new CoreException(s);
        } finally {
        }
    }

    @Override
    protected void doSaveDocument(final IProgressMonitor monitor, final Object element, final IDocument document, boolean overwrite) throws CoreException {
        if (element instanceof IFileEditorInput && document instanceof FB2Document) {

            final FB2Document doc = (FB2Document) document;
            final IFileEditorInput input = (IFileEditorInput) element;

            final FileInfo info = (FileInfo) getElementInfo(element);
            final IFile file = input.getFile();

            InputStream stream;

            byte[] bytes;
            bytes = doc.getXmlString().getBytes();
            stream = new ByteArrayInputStream(bytes);

            if (file.exists()) {

                if (info != null && !overwrite) {
                    checkSynchronizationState(info.fModificationStamp, file);
                }

                // inform about the upcoming content change
                fireElementStateChanging(element);
                try {
                    file.setContents(stream, overwrite, true, monitor);
                } catch (final CoreException x) {
                    // inform about failure
                    fireElementStateChangeFailed(element);
                    throw x;
                } catch (final RuntimeException x) {
                    // inform about failure
                    fireElementStateChangeFailed(element);
                    throw x;
                }

            } else {
                try {
                    monitor.beginTask("Saving...", 2000);
                    final ContainerCreator creator = new ContainerCreator(file.getWorkspace(), file.getParent().getFullPath());
                    creator.createContainer(new SubProgressMonitor(monitor, 1000));
                    file.create(stream, false, new SubProgressMonitor(monitor, 1000));
                } finally {
                    monitor.done();
                }
            }

        } else {
            super.doSaveDocument(monitor, element, document, overwrite);
        }
    }

}