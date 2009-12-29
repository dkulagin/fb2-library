@Echo off

java -Xms256M -Xmx512M -jar @jar.file@ ca -input %1 -output %2 -depth %3 -distance %4 -include-files %5
