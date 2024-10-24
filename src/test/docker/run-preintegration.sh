#!/bin/bash

set -e

docker-compose -f src/test/docker/docker-compose.yml kill || echo "no containers to kill"
docker-compose -f src/test/docker/docker-compose.yml down -v || echo "no volumes to remove"
docker-compose -f src/test/docker/docker-compose.yml rm -s -f -v || echo "no containers to remove"
docker-compose -f src/test/docker/docker-compose.yml build
docker-compose -f src/test/docker/docker-compose.yml up -d

sh src/test/docker/localstack/create-ssm.sh
