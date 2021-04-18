#!/bin/bash

aws ecs update-service --service shop-service-v1 --cluster shop-directory-fargate-cluster --force-new-deployment