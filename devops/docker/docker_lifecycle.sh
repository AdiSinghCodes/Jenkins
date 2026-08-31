#!/bin/bash
# Docker Container Lifecycle Management Script for Employee Helpdesk System
# Student: Aditya Singh (Roll No: 23102B0010)

IMAGE_NAME="adisingh/employee-helpdesk-system"
TAG="v1.0.0"
CONTAINER_NAME="helpdesk-app-container"
PORT="8082"

echo "========================================================"
echo " 1. Building Docker Image: ${IMAGE_NAME}:${TAG}"
echo "========================================================"
docker build -t ${IMAGE_NAME}:${TAG} -t ${IMAGE_NAME}:latest .

echo "========================================================"
echo " 2. Inspecting Local Docker Images"
echo "========================================================"
docker images | grep ${IMAGE_NAME}

echo "========================================================"
echo " 3. Running Container on Port ${PORT}"
echo "========================================================"
docker stop ${CONTAINER_NAME} 2>/dev/null || true
docker rm ${CONTAINER_NAME} 2>/dev/null || true

docker run -d \
  --name ${CONTAINER_NAME} \
  -p ${PORT}:${PORT} \
  --health-cmd="wget --quiet --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1" \
  --health-interval=10s \
  ${IMAGE_NAME}:${TAG}

echo "========================================================"
echo " 4. Verifying Running Containers & Port Mapping"
echo "========================================================"
docker ps --filter "name=${CONTAINER_NAME}"

echo "========================================================"
echo " 5. Inspecting Container Logs"
echo "========================================================"
sleep 5
docker logs --tail 20 ${CONTAINER_NAME}

echo "========================================================"
echo " 6. Performing Application Health Check"
echo "========================================================"
curl -s http://localhost:${PORT}/actuator/health || echo "Waiting for container startup..."

echo "========================================================"
echo " 7. Docker Lifecycle Management Complete!"
echo "========================================================"
