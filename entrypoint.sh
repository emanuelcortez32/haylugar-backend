#!/bin/bash

set -e

startService() {
  java -Xms512m -Xmx1536m -XX:MaxMetaspaceSize=256m -XX:MaxGCPauseMillis=200 \
       -jar /vol/greenbundle/component/jar/$APPLICATION.jar \
       --spring.profiles.active="$ENVIRONMENT"

  exit 0
}

echo "Starting $APPLICATION on $ENVIRONMENT"

startService