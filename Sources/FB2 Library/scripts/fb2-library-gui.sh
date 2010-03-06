#!/bin/bash

SCRIPT=$0
SCRIPT_PATH=`dirname "$SCRIPT"`

java @java.linux.options@ -cp "$SCRIPT_PATH/@jar.file@" org.ak2.fb2.library.Starter $*
