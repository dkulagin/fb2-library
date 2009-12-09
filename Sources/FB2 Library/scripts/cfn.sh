#!/bin/bash

java -Xms256M -Xmx512M -jar @jar.file@ cfn -input $1 -output $2 -outpath Library -outformat ZIP
