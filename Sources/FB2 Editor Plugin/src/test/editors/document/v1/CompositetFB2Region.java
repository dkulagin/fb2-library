package test.editors.document.v1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class CompositetFB2Region extends AbstractFB2Region implements IFB2CompositeRegion {

    private final LinkedList<IFB2Region> fieldChildren = new LinkedList<IFB2Region>();

    @Override
    public List<IFB2Region> getChildren() {
        return fieldChildren;
    }

    @Override
    public void addChild(final IFB2Region child) {
        final IFB2Region last = fieldChildren.getLast();
        if (last != null) {
            last.setNext(child);
        }
        child.setPrevious(last);
        child.setNext(null);
        child.setParent(this);
        fieldChildren.add(child);
        clearLength();
    }

    @Override
    public void removeChild(final IFB2Region child) {
        final int index = fieldChildren.indexOf(child);
        if (index != -1) {
            child.setParent(null);
            final IFB2Region previous = child.getPrevious();
            final IFB2Region next = child.getNext();

            if (previous != null) {
                previous.setNext(next);
            }
            if (next != null) {
                next.setPrevious(previous);
            }
            clearLength();
        }
    }

    @Override
    public List<IFB2TextRegion> getTextSequence() {
        final List<IFB2Region> children = getChildren();
        final List<IFB2TextRegion> texts = new ArrayList<IFB2TextRegion>(children.size());
        for (final IFB2Region child : children) {
            if (child instanceof IFB2TextRegion) {
                texts.add((IFB2TextRegion) child);
            }
        }
        return texts;
    }

    protected void buildTextSequence(final List<IFB2Region> children, final List<IFB2TextRegion> texts) {
        for (final IFB2Region child : children) {
            if (child instanceof IFB2TextRegion) {
                texts.add((IFB2TextRegion) child);
            } else if (child instanceof IFB2CompositeRegion) {
                buildTextSequence(((IFB2CompositeRegion) child).getChildren(), texts);
            }
        }
    }

    @Override
    protected void clearOffset() {
        final IFB2Region first = fieldChildren.getFirst();
        if (first != null) {
            ((AbstractFB2Region) first).clearOffset();
        }
    }

    @Override
    protected Integer calculateLength() {
        int length = 0;
        final List<IFB2Region> children = getChildren();
        for (final IFB2Region region : children) {
            length += region.getLength();
        }
        return length;
    }
}
