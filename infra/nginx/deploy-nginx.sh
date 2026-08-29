#!/bin/bash
# nginx 설정 파일을 서버에 배포하는 스크립트
# 사용법: ./deploy-nginx.sh [dev|prod]

set -e

ENV=${1:-dev}
KEY=~/documents/poti/poti-key.pem
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ "$ENV" = "dev" ]; then
  HOST=ubuntu@${DEV_HOST:?DEV_HOST 환경변수를 설정하세요 (export DEV_HOST=<IP>)}
  SITES="dev-app.poti.kr dev.poti.kr"
elif [ "$ENV" = "prod" ]; then
  HOST=ubuntu@${PROD_HOST:?PROD_HOST 환경변수를 설정하세요 (export PROD_HOST=<IP>)}
  SITES="app.poti.kr"
else
  echo "사용법: $0 [dev|prod]"
  exit 1
fi

echo "### [$ENV] nginx 설정 배포 시작"

# service-url.inc 없으면 초기값 생성 (신규 서버 대응)
ssh -i "$KEY" "$HOST" \
  "[ -f /etc/nginx/conf.d/service-url.inc ] || echo 'set \$service_url http://127.0.0.1:8080;' | sudo tee /etc/nginx/conf.d/service-url.inc"

# sites-available 복사
for SITE in $SITES; do
  echo "### $SITE 설정 복사..."
  scp -i "$KEY" "$SCRIPT_DIR/sites-available/$SITE" "$HOST:/tmp/$SITE"
  ssh -i "$KEY" "$HOST" "sudo cp /tmp/$SITE /etc/nginx/sites-available/$SITE && \
    sudo ln -sf /etc/nginx/sites-available/$SITE /etc/nginx/sites-enabled/$SITE"
done

# nginx 문법 검사 및 reload (연결 중단 없이 설정 반영)
echo "### nginx 문법 검사..."
ssh -i "$KEY" "$HOST" "sudo nginx -t && sudo systemctl reload nginx"

echo "### ✅ 배포 완료"
