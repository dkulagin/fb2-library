
FB2Library in an utility provides various functions to work with org.ak2.fb2.library in FB2 format.

Usage: java -jar fb2.library-<version>.jar <command> [options].

Where command is:

  cfn - convert file names. This command renames all files from input folder into output folder.
        The following options are supported:
        -input  <original book file or folder> - input file or folder
        -output <target folder> - folder to store renamed book
        -outpath <output path type> - output path type. The following are supported:
                Simple   - Book will be saved into the output folder indirectly
                Standard - Book will be save into an author/sequence sub-folder in the output folder.
                Library - Book will be save into an a/author/sequence sub-folder in the output folder.
        -outformat <output book format> - output book format. The following are supported:
                FB2 - uncompressed fb2 book
                ZIP - fb2 book in a separated zip archive

  enc - fix XML encoding. This command replace files with wrong XML encoding.
        The following options are supported:
        -input  <library folder> - library folder
        -outformat <output book format> - output book format. The following are supported:
                FB2 - uncompressed fb2 book
                ZIP - fb2 book in a separated zip archive

  ca - check authors in renamed library.
       The following options are supported:
       -input  <library folder> - library folder
       -output <target file> - file with list of similiar author name
       -depth <depth> - search depth (default 0)
       -distance <distance> - levenstein distance (default 1)