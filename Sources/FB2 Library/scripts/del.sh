#!/bin/bash

SCRIPT=$0
SCRIPT_PATH=`dirname "$SCRIPT"`

java @java.linux.options@ -jar "$SCRIPT_PATH/@jar.file@" del -input $1
