@Echo off

java @java.win.options@ -jar @jar.file@ ma -input %1 -output %2 -outpath Library -outformat ZIP
