#!/bin/bash

CSV_PATH=$1

if [[ -z "$CSV_PATH" ]]; then
    echo "Must provide Merchant CSV_PATH" 1>&2
    exit 1
fi

if [[ -z "$API_KEY" ]]; then
    echo "Must set the environment variable API_KEY" 1>&2
    exit 1
fi

./../mvnw install -DskipTests

java -jar target/clients-1.0-SNAPSHOT.jar $API_KEY $CSV_PATH categories