package test.editors.document.v1;

@Deprecated
public interface IFB2TextRegion extends IFB2Region {

    String getText();

    void setText(String text);

    void appendText(String text);

    IFB2TextRegion getPrevText();

    IFB2TextRegion getNextText();
}
