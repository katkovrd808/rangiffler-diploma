#!/bin/bash

PORTS=(9000 8089 8081 8082 8083)

for PORT in "${PORTS[@]}"; do
    PID=$(lsof -ti :$PORT 2>/dev/null)

    if [ ! -z "$PID" ]; then
        kill -9 $PID 2>/dev/null

        sleep 1
        CHECK_PID=$(lsof -ti :$PORT 2>/dev/null)
        if [ -z "$CHECK_PID" ]; then
            echo "Port $PORT killed"
        else
            echo "Can't kill port $PORT"
        fi
    else
        echo "Port $PORT available"
    fi
done