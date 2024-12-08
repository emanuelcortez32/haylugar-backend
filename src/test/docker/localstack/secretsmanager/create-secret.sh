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


create_or_update_secret "/local/mercadopago/secret"                     '{"accessToken":"TEST-465192634162721-093022-e1835469ad4eb214d6074a6068c09bc5-255810323"}'

echo "Secrets operation completed!.."
echo "----------------------------------------------------------"