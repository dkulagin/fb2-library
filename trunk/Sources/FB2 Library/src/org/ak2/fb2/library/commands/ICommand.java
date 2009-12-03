package org.ak2.fb2.library.commands;

import org.ak2.fb2.library.exceptions.LibraryException;

public interface ICommand {

    String PARAM_INPUT = "input";

    String PARAM_OUTPUT = "output";

    String PARAM_OUTFORMAT = "outformat";

    String PARAM_OUTPATH = "outpath";

    String getName();

    void execute(CommandArgs args) throws LibraryException;

}
