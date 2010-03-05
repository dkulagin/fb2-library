package test.editors.document.v1;

import org.w3c.dom.Node;

import test.editors.document.v0.FB2DocumentPartitioner;

@Deprecated
public abstract class AbstractFB2Region implements IFB2Region {

    private Node fieldNode;

    private IFB2CompositeRegion fieldParent;

    private IFB2Region fieldNext;

    private IFB2Region fieldPrev;

    private Integer fieldOffset;

    private Integer fieldLength;

    @Override
    public String getType() {
        return FB2DocumentPartitioner.CONTENT_TYPE;
    }

    @Override
    public int getLength() {
        if (fieldLength == null) {
            fieldLength = calculateLength();
        }
        return fieldLength;
    }

    @Override
    public int getOffset() {
        if (fieldOffset == null) {
            fieldOffset = calculateOffset();
        }
        return fieldOffset;
    }

    @Override
    public Node getNode() {
        return fieldNode;
    }

    @Override
    public IFB2CompositeRegion getParent() {
        return fieldParent;
    }

    /**
     * @param parent
     * @see test.editors.document.v1.IFB2Region#setParent(test.editors.document.v1.IFB2Region)
     */
    @Override
    public void setParent(final IFB2CompositeRegion parent) {
        final IFB2CompositeRegion oldParent = fieldParent;
        fieldParent = parent;
        if (oldParent != null) {
            oldParent.removeChild(this);
        }
        clearOffset();
    }

    /**
     * @param prev
     * @see test.editors.document.v1.IFB2Region#setPrevious(test.editors.document.v1.IFB2Region)
     */
    @Override
    public void setPrevious(final IFB2Region prev) {
        fieldPrev = prev;
        clearOffset();
    }

    @Override
    public IFB2Region getNext() {
        return fieldNext;
    }

    @Override
    public void setNext(final IFB2Region next) {
        fieldNext = next;
    }

    @Override
    public IFB2Region getPrevious() {
        return fieldPrev;
    }

    protected void clearOffset() {
        fieldOffset = null;
        if (fieldNext != null) {
            ((AbstractFB2Region) fieldNext).clearOffset();
        }
    }

    protected void clearLength() {
        fieldLength = null;
        if (fieldParent != null) {
            ((AbstractFB2Region) fieldParent).clearLength();
        }
        if (fieldNext != null) {
            ((AbstractFB2Region) fieldNext).clearOffset();
        }
    }

    protected Integer calculateOffset() {
        if (fieldPrev != null) {
            return fieldPrev.getOffset() + fieldPrev.getLength();
        }
        if (fieldParent != null) {
            return fieldParent.getOffset();
        }
        return 0;
    }

    protected abstract Integer calculateLength();
}
