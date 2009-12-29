#!/bin/bash

SCRIPT=$0
SCRIPT_PATH=`dirname "$SCRIPT"`

java -Xms256M -Xmx512M -jar "$SCRIPT_PATH/@jar.file@" del -input $1
