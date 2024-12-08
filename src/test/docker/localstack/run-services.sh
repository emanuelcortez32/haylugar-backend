#!/bin/bash

cd "$(dirname "$0")"

sh ./ssm/create-ssm.sh
sh ./secretsmanager/create-secret.sh