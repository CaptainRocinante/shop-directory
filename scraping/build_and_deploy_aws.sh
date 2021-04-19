#!/bin/bash

./../mvnw install -DskipTests
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 785297363968.dkr.ecr.us-east-1.amazonaws.com

# Build and Push the Scraper Application
docker build -t scraper .
docker tag scraper:latest 785297363968.dkr.ecr.us-east-1.amazonaws.com/scraper:latest
docker push 785297363968.dkr.ecr.us-east-1.amazonaws.com/scraper:latest

# Force a new deployment
aws ecs update-service --service scraper-service --cluster shop-directory-fargate-cluster \
--force-new-deployment > ecs.log