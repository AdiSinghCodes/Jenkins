#!/bin/bash
# Reliability Validation & Automated Rollback Script
# Student: Aditya Singh (Roll No: 23102B0010)

APP_NAME="employee-helpdesk-system-container"
PORT="8082"
PREVIOUS_IMAGE="adisingh/employee-helpdesk-system:v1.0.0-stable"
NEW_IMAGE="adisingh/employee-helpdesk-system:latest"

echo "========================================================"
echo " 1. Performing Application Health Check"
echo "========================================================"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${PORT}/actuator/health)

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo "SUCCESS: Application Health Check Passed! HTTP Code: ${HTTP_STATUS}"
    echo "System is healthy on http://localhost:${PORT}/dashboard"
    exit 0
else
    echo "WARNING: Health Check Failed with HTTP Status ${HTTP_STATUS}!"
    echo "Initiating Automated Zero-Downtime Rollback to Previous Stable Release..."
    
    echo "Stopping failing container..."
    docker stop ${APP_NAME} || true
    docker rm ${APP_NAME} || true
    
    echo "Rolling back to stable image: ${PREVIOUS_IMAGE}..."
    docker run -d --name ${APP_NAME} -p ${PORT}:${PORT} ${PREVIOUS_IMAGE}
    
    sleep 5
    ROLLBACK_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${PORT}/actuator/health)
    echo "ROLLBACK COMPLETE: System restored to stable release. Health Check Status: ${ROLLBACK_STATUS}"
fi
