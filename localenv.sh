#!/bin/bash

export SPRING_PROFILES_ACTIVE=local

ZOOKEEPER_PORT=2181

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRADLEW_PATH=$(find "$SCRIPT_DIR" -name "gradlew" -type f | head -1)

if [ -z "$GRADLEW_PATH" ]; then
    echo "gradlew not found! Make sure you're located in project directory"
    exit 1
fi
PROJECT_ROOT="$(dirname "$GRADLEW_PATH")"
cd "$PROJECT_ROOT" || exit 1

if [ ! -f "gradlew" ]; then
    echo "gradlew not found!"
    exit 1
fi

wait_for_service() {
    local host=$1
    local port=$2
    local name=$3
    local max_attempts=30
    local attempt=1

    echo "### Waiting for $name ($host:$port) ###"

    while [ $attempt -le $max_attempts ]; do
        if nc -z $host $port 2>/dev/null; then
            return 0
        fi

        sleep 2
        ((attempt++))
    done

    return 1
}

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
  -e ZOOKEEPER_CLIENT_PORT=${ZOOKEEPER_PORT} \
  -e ZOOKEEPER_TICK_TIME=2000 \
  -e ZOOKEEPER_SYNC_LIMIT=2 \
  confluentinc/cp-zookeeper:7.3.2

ZOOKEEPER_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' zookeeper)

docker run --name=kafka \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=${ZOOKEEPER_IP}:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -p 9092:9092 \
  -d confluentinc/cp-kafka:7.3.2

wait_for_service "127.0.0.1" "9092" "Kafka" || exit 1

./gradlew rangiffler-auth:bootRun -Dspring.profiles.active=local &
wait_for_service "127.0.0.1" "9000" "Auth"

./gradlew rangiffler-userdata:bootRun -Dspring.profiles.active=local &
wait_for_service "127.0.0.1" "8081" "Userdata"

./gradlew rangiffler-countries:bootRun -Dspring.profiles.active=local &
wait_for_service "127.0.0.1" "8082" "Countries"

./gradlew rangiffler-photos:bootRun -Dspring.profiles.active=local &
wait_for_service "127.0.0.1" "8083" "Photos"

./gradlew rangiffler-gateway:bootRun -Dspring.profiles.active=local
wait_for_service "127.0.0.1" "8080" "Gateway"