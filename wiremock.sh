#!/bin/bash

docker run --name rangiffler-mock \
  -p 8089:8089 \
  -v ./wiremock/rest:/home/wiremock \
  -d wiremock/wiremock:2.35.0 \
  --global-response-templating \
  --enable-stub-cors