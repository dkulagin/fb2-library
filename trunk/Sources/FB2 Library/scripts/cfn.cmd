@Echo off

java @java.win.options@ -jar @jar.file@ cfn -input %1 -output %2 -outpath Library -outformat ZIP
