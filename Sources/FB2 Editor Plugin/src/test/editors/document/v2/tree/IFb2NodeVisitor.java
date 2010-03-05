package test.editors.document.v2.tree;

import test.editors.document.v2.IFb2Node;

public interface IFb2NodeVisitor {

    Result handle(IFb2Node node);

    public enum Result {
        Stop, ProcessOnlyChildren, NextSibling, Continue;
    }

}
