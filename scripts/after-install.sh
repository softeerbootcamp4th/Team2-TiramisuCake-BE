#!/bin/bash

# 경로 설정
APP_DIR="/home/ubuntu/backend/build/libs"
YML_PATH="/home/ubuntu/backend/src/main/resources/yml/application-test.yml"
PORT=5000

# 포트가 사용 중인지 확인하고, 사용 중이면 해당 프로세스를 종료
PID=$(lsof -ti:$PORT)
if [ -n "$PID" ]; then
  echo "Port $PORT is in use. Killing process $PID..."
  kill -9 $PID
else
  echo "Port $PORT is free."
fi

# JAR 파일 실행
JAR_FILE="$APP_DIR/backend-0.0.1-SNAPSHOT.jar"
if [ -f "$JAR_FILE" ]; then
  echo "Starting the application..."
  nohup java -Duser.timezone=Asia/Seoul -jar $JAR_FILE --spring.config.additional-location=file:$YML_PATH > /home/ubuntu/backend/app.log 2>&1 &
else
  echo "JAR file not found: $JAR_FILE"
  exit 1
fi