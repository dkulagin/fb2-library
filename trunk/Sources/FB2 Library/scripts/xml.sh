#!/bin/bash

SCRIPT=$0
SCRIPT_PATH=`dirname "$SCRIPT"`

java @java.linux.options@ -jar "$SCRIPT_PATH/@jar.file@" xml -input $1 -output $2
