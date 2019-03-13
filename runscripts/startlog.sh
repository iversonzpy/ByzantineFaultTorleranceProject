#!/bin/bash
# logger.sh
# Log everything you do in Terminal.

#* Formatted date & time string.
FORMATTED_DATE=`/bin/date "+%Y-%m-%d%H%M%S"`

#* Archive the previous file
/bin/cp -f ./log/YCSBClientLog.log{,.$FORMATTED_DATE.txt}

#* Begin a new one
/usr/bin/script ./log/YCSBClientLog.log