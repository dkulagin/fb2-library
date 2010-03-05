package test.editors.document.v1;

import java.util.List;

@Deprecated
public interface IFB2CompositeRegion extends IFB2Region {

    void addChild(IFB2Region child);

    void removeChild(IFB2Region child);

    List<IFB2Region> getChildren();

    List<IFB2TextRegion> getTextSequence();
}
