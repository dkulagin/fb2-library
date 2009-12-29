#!/bin/bash

SCRIPT=$0
SCRIPT_PATH=`dirname "$SCRIPT"`

java -Xms256M -Xmx512M -jar "$SCRIPT_PATH/@jar.file@" ca -input $1 -output $2 -depth $3 -distance $4 -include-files $5
