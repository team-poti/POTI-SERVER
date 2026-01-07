#!/bin/bash

# 1. 현재 떠있는 컨테이너 확인
IS_GREEN=$(docker ps | grep poti-green)
DEFAULT_CONF="/etc/nginx/conf.d/service-url.inc"

if [ -z "$IS_GREEN" ]; then
  echo "### Blue => Green 배포"
  TARGET_CONTAINER="poti-green"
  TARGET_PORT=8081
  STOP_CONTAINER="poti-blue"
else
  echo "### Green => Blue 배포"
  TARGET_CONTAINER="poti-blue"
  TARGET_PORT=8080
  STOP_CONTAINER="poti-green"
fi

# 2. 이미지 받기 및 실행
echo "### 1. 이미지 Pull..."
docker-compose pull $TARGET_CONTAINER
echo "### 2. 컨테이너 실행..."
docker-compose up -d $TARGET_CONTAINER

# 3. 헬스 체크
echo "### 3. Health Check (서버 뜰 때까지 대기)..."
for i in {1..10}; do
  response=$(curl -s http://127.0.0.1:$TARGET_PORT/health) # /health 없으면 메인(/)으로
  if [ "$response" == "OK" ] || [ "$response" == '{"status":"UP"}' ]; then
    echo "### ✅ 서버 정상 구동"
    break
  else
    echo "### ⏳ 대기 중... ($i/10)"
    sleep 10
  fi
done

# 4. Nginx 스위칭
echo "### 4. Nginx 포트 변경 ($TARGET_PORT) 및 Reload..."
echo "set \$service_url http://127.0.0.1:$TARGET_PORT;" | sudo tee $DEFAULT_CONF
sudo nginx -s reload

# 5. 기존 컨테이너 종료 (5초 대기)
echo "### ⏳ 기존 요청 처리 대기 (5초)..."
sleep 5
echo "### 5. 기존 컨테이너 중단..."
docker-compose stop $STOP_CONTAINER
docker-compose rm -f $STOP_CONTAINER

echo "### 🎉 배포 완료!"