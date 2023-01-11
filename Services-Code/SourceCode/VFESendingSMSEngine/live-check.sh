#!/bin/bash          
FILE="/opt/docker/LIVENESS.log"
OUT=$(tail -1 "$FILE")
RUNNING="Engine is running normally"
NOT_RUNNING="Engine is not running normally"
if [[ "$OUT" == *"$RUNNING"* ]]
then
	$(cp /dev/null "$FILE")
	exit 0
elif [[ "$OUT" == *"$NOT_RUNNING"* ]]
then
	exit 1
else
	exit 0
fi
