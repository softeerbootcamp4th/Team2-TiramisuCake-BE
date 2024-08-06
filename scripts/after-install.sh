#!/bin/bash

# 경로 설정
APP_DIR="/home/ubuntu/backend/build/libs"
YML_PATH="/home/ubuntu/backend/src/main/resources/yml/application-prod.yml"

# JAR 파일 실행
JAR_FILE="$APP_DIR/backend-0.0.1-SNAPSHOT.jar"
if [ -f "$JAR_FILE" ]; then
  echo "Starting the application..."
  nohup java -jar $JAR_FILE --spring.config.additional-location=file:$YML_PATH > /home/ubuntu/backend/app.log 2>&1 &
else
  echo "JAR file not found: $JAR_FILE"
  exit 1
fi