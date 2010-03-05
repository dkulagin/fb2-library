package test.editors.document.v2;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.editors.document.v2.impl.BodyImpl;
import test.editors.document.v2.impl.DocumentImpl;
import test.editors.document.v2.impl.EmphasisImpl;
import test.editors.document.v2.impl.EmptyLineImpl;
import test.editors.document.v2.impl.EpigraphAuthorImpl;
import test.editors.document.v2.impl.EpigraphImpl;
import test.editors.document.v2.impl.ImageImpl;
import test.editors.document.v2.impl.ParagraphImpl;
import test.editors.document.v2.impl.SectionImpl;
import test.editors.document.v2.impl.StrongImpl;
import test.editors.document.v2.impl.SubtitleImpl;
import test.editors.document.v2.impl.TextImpl;
import test.editors.document.v2.impl.TitleImpl;
import test.editors.document.v2.impl.XmlTag;
import test.editors.document.v2.tree.Fb2TreeWalker;
import test.editors.document.v2.tree.IFb2NodeVisitor;

public class Fb2DocumentFactory {

    private static final Class<?>[] CLASSES = { BodyImpl.class, DocumentImpl.class, EmphasisImpl.class, EmptyLineImpl.class, EpigraphAuthorImpl.class,
            EpigraphImpl.class, ImageImpl.class, ParagraphImpl.class, SectionImpl.class, StrongImpl.class, SubtitleImpl.class, TextImpl.class, TitleImpl.class,

    };

    private static Map<String, Class<? extends IFb2Node>> staticMap;

    public static IFb2Document create(final Document xmlDocument) {
        if (xmlDocument == null) {
            return null;
        }

        try {
            final IFb2Node root = create(null, xmlDocument.getDocumentElement());
            if (root instanceof IFb2Document) {
                final IFb2Document document = (IFb2Document) root;
                build(document);
                return document;
            }
        } catch (final Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    private static void build(final IFb2Document root) {
        Fb2TreeWalker.visit(root, new Creator());
    }

    private static IFb2Node create(final IFb2CompositeNode parent, final Node xmlNode) {
        final Class<? extends IFb2Node> clazz = getClass(xmlNode);
        if (clazz != null) {
            try {
                boolean create = true;
                final Node parentNode = xmlNode.getParentNode();
                final String parentNodeName = parentNode != null ? parentNode.getNodeName() : "";
                final String[] parents = clazz.getAnnotation(XmlTag.class).parents();
                if (parents != null && parents.length > 0) {
                    create = false;
                    for (int i = 0; !create && i < parents.length; i++) {
                        if (parents[i].equals(parentNodeName)) {
                            create = true;
                        }
                    }
                }
                if (create) {
                    final Constructor<? extends IFb2Node> constructor = clazz.getConstructor(IFb2CompositeNode.class, Node.class);
                    return constructor.newInstance(parent, xmlNode);
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static Class<? extends IFb2Node> getClass(final Node xmlNode) {
        final Map<String, Class<? extends IFb2Node>> map = getMap();
        return map.get(xmlNode.getNodeName());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Class<? extends IFb2Node>> getMap() {
        if (staticMap == null) {
            staticMap = new HashMap<String, Class<? extends IFb2Node>>();

            for (final Class<?> clazz : CLASSES) {
                final XmlTag annotation = clazz.getAnnotation(XmlTag.class);
                if (annotation != null) {
                    final String value = annotation.name();
                    if (value != null && value.length() > 0) {
                        staticMap.put(value, (Class<? extends IFb2Node>) clazz);
                    }
                }
            }
        }
        return staticMap;
    }

    private static class Creator implements IFb2NodeVisitor {

        @Override
        public Result handle(final IFb2Node node) {
            if (node instanceof IFb2CompositeNode) {
                final IFb2CompositeNode parent = (IFb2CompositeNode) node;
                final Node xmlNode = node.getXmlNode();
                final NodeList childNodes = xmlNode.getChildNodes();
                for (int index = 0; index < childNodes.getLength(); index++) {
                    final Node item = childNodes.item(index);
                    final IFb2Node child = create(parent, item);
                    if (child != null) {
                        parent.getChildren().add(child);
                    }
                }
            }
            return Result.Continue;
        }
    }
}
