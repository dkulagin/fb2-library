@Echo off

java @java.win.options@ -jar @jar.file@ ca -input %1 -output %2 -depth %3 -distance %4 -include-files %5
