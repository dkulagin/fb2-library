package test.editors.document.v2;

public interface IFb2StyleNode extends IFb2Node {

    String FB2_TITLE_STYLE = "fb2/title";

    String FB2_SUBTITLE_STYLE = "fb2/subtitle";

    String FB2_SECTION_STYLE = "fb2/section";

    String FB2_EPIGRAPH_STYLE = "fb2/epigraph";

    String FB2_EPIGRAPH_AUTHOR_STYLE = "fb2/epigraph_author";

    String FB2_TEXT_STYLE = "fb2/text";

    String FB2_IMAGE_STYLE = "fb2/image";

    String FB2_STRONG_STYLE = "fb2/strong";

    String FB2_EMPHASIS_STYLE = "fb2/emphasis";

    public String getStyleId();
}
