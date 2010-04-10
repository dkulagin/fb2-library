package org.ak2.fb2.library.commands.cfn;

import org.junit.Test;


public class RenameFilesTest {

    @Test
    public void testSeqNo() {
        RenameFiles cmd = new RenameFiles();
        
        cmd.getBookFileName("Test book name", "null");
    }
}
