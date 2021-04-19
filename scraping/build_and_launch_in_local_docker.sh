#!/bin/bash

./../mvnw install -DskipTests
docker build -t scraper .
docker run --net=host --env-file .env.local -p 8081:8081 scraper .