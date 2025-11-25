#!/bin/bash

docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

docker run --name rangiffler-all -p 5432:5432 \
  -e POSTGRES_PASSWORD=secret \
  -v pgdata:/var/lib/postgresql/data \
  -v ./postgres/script:/docker-entrypoint-initdb.d \
  -e CREATE_DATABASES=rangiffler-auth,rangiffler-countries,rangiffler-userdata,rangiffler-photos \
  -e TZ=GMT+3 \
  -e PGTZ=GMT+3 \
  -d postgres:15.1 \
  -c max_prepared_transactions=100

docker run -d -p 2181:2181 --name=zookeeper \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  -e ZOOKEEPER_TICK_TIME=2000 \
  -e ZOOKEEPER_SYNC_LIMIT=2 \
  confluentinc/cp-zookeeper:7.3.2

ZOOKEEPER_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' zookeeper)

docker run --name=kafka \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=${ZOOKEEPER_IP}:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -p 9092:9092 \
  -d confluentinc/cp-kafka:7.3.2