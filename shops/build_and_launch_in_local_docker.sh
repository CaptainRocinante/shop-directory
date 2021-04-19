#!/bin/bash

./../mvnw install -DskipTests
docker build -t shopdirectory .
docker run --net=host --env-file .env.local -p 8080:8080 shopdirectory .