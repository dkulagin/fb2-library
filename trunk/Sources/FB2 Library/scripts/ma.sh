#!/bin/bash

SCRIPT=$0
SCRIPT_PATH=`dirname "$SCRIPT"`

java -Xms256M -Xmx512M -jar "$SCRIPT_PATH/@jar.file@" ma -input $1 -output $2 -outpath Library -outformat ZIP
