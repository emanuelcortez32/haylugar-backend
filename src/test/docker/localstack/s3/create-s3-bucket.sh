#!/bin/bash

BUCKET_NAME="local-hay-lugar-images"
ENDPOINT_URL="http://localhost:4566"

echo "Creating bucket $BUCKET_NAME..."

docker exec -it aws-local-stack /bin/bash