#!/usr/bin/env bash
set -euo pipefail

TABLE_NAME="${CART_TABLE_NAME:-cart_details}"
ENDPOINT_URL="${DYNAMODB_ENDPOINT_URL:-http://localhost:8000}"
AWS_REGION="${AWS_REGION:-ap-south-1}"

export AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID:-local}"
export AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY:-local}"

if aws dynamodb describe-table \
  --table-name "${TABLE_NAME}" \
  --endpoint-url "${ENDPOINT_URL}" \
  --region "${AWS_REGION}" >/dev/null 2>&1; then
  echo "DynamoDB table already exists: ${TABLE_NAME}"
else
  aws dynamodb create-table \
    --table-name "${TABLE_NAME}" \
    --attribute-definitions AttributeName=cart_id,AttributeType=S \
    --key-schema AttributeName=cart_id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url "${ENDPOINT_URL}" \
    --region "${AWS_REGION}" >/dev/null

  aws dynamodb wait table-exists \
    --table-name "${TABLE_NAME}" \
    --endpoint-url "${ENDPOINT_URL}" \
    --region "${AWS_REGION}"

  echo "Created DynamoDB table: ${TABLE_NAME}"
fi
