#!/bin/bash
PIDS=`ps -ef|grep java|grep server/|awk '{print $2 }'`
if [ -n "$PIDS" ]; then
	echo Killing $PIDS
	echo $PIDS | xargs kill -9
else
	echo "Spaced server not running"
fi

