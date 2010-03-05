package test.editors.document.v2.impl;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import test.editors.Base64;
import test.editors.document.v2.IFb2Body;
import test.editors.document.v2.IFb2CompositeNode;
import test.editors.document.v2.IFb2Document;
import test.editors.document.v2.IFb2OutlineNode;

@XmlTag(name = "FictionBook")
public class DocumentImpl extends AbstractCompositeNode implements IFb2Document {

    private Map<String, WeakReference<Image>> fieldImages = new HashMap<String, WeakReference<Image>>();

    public DocumentImpl(final IFb2CompositeNode parent, final Node xmlNode) {
        super(null, xmlNode);
    }

    @Override
    public String getTitle() {
        return getTagName();
    }

    @Override
    public List<IFb2Body> getBodies() {
        return getFiltered(IFb2Body.class);
    }

    @Override
    public List<IFb2OutlineNode> getInner() {
        return getFiltered(IFb2OutlineNode.class);
    }

    public Image getImage(String imageId) {
        WeakReference<Image> weakReference = fieldImages.get(imageId);
        Image image = weakReference != null ? weakReference.get() : null;

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            Object result = xpath.evaluate("/FictionBook/binary[@id='" + imageId + "']/text()", getXmlNode(), XPathConstants.NODE);
            if (result instanceof Text) {
                Text element = (Text) result;
                image = new Image(Display.getDefault(), new ByteArrayInputStream(Base64.decode(element.getNodeValue())));
                fieldImages.put(imageId, new WeakReference<Image>(image));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return image;
    }

}
