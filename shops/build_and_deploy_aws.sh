#!/bin/bash

./../mvnw install -DskipTests
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 785297363968.dkr.ecr.us-east-1.amazonaws.com

# Build and Push the ShopDirectory Application
docker build -t shopdirectory .
docker tag shopdirectory:latest 785297363968.dkr.ecr.us-east-1.amazonaws.com/shopdirectory:latest
docker push 785297363968.dkr.ecr.us-east-1.amazonaws.com/shopdirectory:latest

# Force a new deployment
aws ecs update-service --service shop-service-v1 --cluster shop-directory-fargate-cluster \
--force-new-deployment > ecs.log
