#!/bin/bash
function create_or_update_secret() {
    local secret_name="$1"
    local secret_value="$2"

    printf "[$secret_name]"

    docker exec aws-local-stack awslocal secretsmanager get-secret-value --secret-id "$secret_name" > /dev/null 2>&1

    if [ $? -eq 0 ]; then
      docker exec aws-local-stack awslocal secretsmanager update-secret --secret-id "$secret_name" --secret-string "$secret_value" > /dev/null
      printf "%*s UPDATED                  \n" $((75 - ${#param_name})) ""
    else
      docker exec aws-local-stack awslocal secretsmanager create-secret --name "$secret_name" --secret-string "$secret_value" > /dev/null
      printf "%*s CREATED                  \n" $((75 - ${#param_name})) ""
    fi
}

echo "----------------------------------------------------------"
echo "Creating or updating Secrets in localstack..."

printf "NAME"
printf "%*sSTATUS\n" $((75 - ${#secret_name})) ""


create_or_update_secret "/local/mercadopago/secret"                     '{"your_secret":""}'

echo "Secrets operation completed!.."
echo "----------------------------------------------------------"