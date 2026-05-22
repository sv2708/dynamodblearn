# Local DynamoDB (Docker)

This folder provides a local DynamoDB setup using Docker Compose.

## What this gives you

- DynamoDB Local on `localhost:8000`
- Persistent data in `dynamodb/data/`
- Shared DB mode (`-sharedDb`) so all credentials/regions use one local DB file

## Start

From the repository root:

```bash
docker compose -f dynamodb/docker-compose.yml up -d
```

## Create schema

Create the `cart_details` table:

```bash
./dynamodb/schema/create-cart-details-table.sh
```

The script is idempotent. It uses these defaults, which you can override with environment variables:

```text
CART_TABLE_NAME=cart_details
DYNAMODB_ENDPOINT_URL=http://localhost:8000
AWS_REGION=ap-south-1
```

## Stop

```bash
docker compose -f dynamodb/docker-compose.yml down
```

## View logs

```bash
docker compose -f dynamodb/docker-compose.yml logs -f dynamodb-local
```

## Reset local data

Stop the container, then remove persisted files:

```bash
docker compose -f dynamodb/docker-compose.yml down
rm -rf dynamodb/data/*
```

## Quick connectivity check (optional)

```bash
aws dynamodb list-tables \
  --endpoint-url http://localhost:8000 \
  --region us-east-1
```

If you do not have AWS CLI configured, set dummy credentials for local use (for example `AWS_ACCESS_KEY_ID=local` and `AWS_SECRET_ACCESS_KEY=local`).
