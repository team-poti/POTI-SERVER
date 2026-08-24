#!/bin/bash
# nginx 설정 파일을 서버에 배포하는 스크립트
# 사용법: ./deploy-nginx.sh [dev|prod]

set -e

ENV=${1:-dev}
KEY=~/documents/poti/poti-key.pem
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ "$ENV" = "dev" ]; then
  HOST=ubuntu@3.34.207.84
  SITES="dev-app.poti.kr dev.poti.kr"
elif [ "$ENV" = "prod" ]; then
  HOST=ubuntu@${PROD_HOST:?PROD_HOST 환경변수를 설정하세요}
  SITES="app.poti.kr"
else
  echo "사용법: $0 [dev|prod]"
  exit 1
fi

echo "### [$ENV] nginx 설정 배포 시작"

# sites-available 복사
for SITE in $SITES; do
  echo "### $SITE 설정 복사..."
  scp -i "$KEY" "$SCRIPT_DIR/sites-available/$SITE" "$HOST:/tmp/$SITE"
  ssh -i "$KEY" "$HOST" "sudo cp /tmp/$SITE /etc/nginx/sites-available/$SITE && \
    sudo ln -sf /etc/nginx/sites-available/$SITE /etc/nginx/sites-enabled/$SITE"
done

# nginx 문법 검사 및 재시작
echo "### nginx 문법 검사..."
ssh -i "$KEY" "$HOST" "sudo nginx -t && sudo systemctl restart nginx"

echo "### ✅ 배포 완료"
