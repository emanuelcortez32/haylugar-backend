#!/bin/bash

set -e

startService() {
  java -Xms256m -Xmx512m \
       -jar /vol/greenbundle/component/jar/$APPLICATION.jar \
       --spring.profiles.active="$ENVIRONMENT"

  exit 0
}

echo "Starting $APPLICATION on $ENVIRONMENT"

startService