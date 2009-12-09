#!/bin/bash

java -Xms256M -Xmx512M -jar fb2.library-1.0-rc2.jar cfn -input $1 -output $2 -outpath Library -outformat ZIP
