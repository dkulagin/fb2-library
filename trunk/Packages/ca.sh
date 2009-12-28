#!/bin/bash

java -Xms256M -Xmx512M -jar fb2.library-1.0-rc4.jar ca -input $1 -output $2 -depth $3 -distance $4
