#!/bin/bash

sleep 5

function create_or_update_ssm_parameter() {
    local param_name="$1"
    local param_value="$2"

    printf "[$param_name]"

    docker exec aws-local-stack awslocal ssm get-parameter --name "$param_name" > /dev/null 2>&1

    if [ $? -eq 0 ]; then
      docker exec aws-local-stack awslocal ssm put-parameter --name "$param_name" --value "$param_value" --type String --overwrite > /dev/null
      printf "%*s UPDATED                  \n" $((75 - ${#param_name})) ""
    else
      docker exec aws-local-stack awslocal ssm put-parameter --name "$param_name" --value "$param_value" --type String > /dev/null
      printf "%*s CREATED                  \n" $((75 - ${#param_name})) ""
    fi
}

sleep 5

echo "----------------------------------------------------------"
echo "Creating or updating SSM params in localstack..."

printf "NAME"
printf "%*sSTATUS\n" $((75 - ${#param_name})) ""


create_or_update_ssm_parameter "/local/mercadopago/token"                     "{your_mp_secret_key}"

echo "SSM operation completed!.."
echo "----------------------------------------------------------"