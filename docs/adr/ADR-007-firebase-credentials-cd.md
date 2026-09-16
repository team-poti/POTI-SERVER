# ADR-007: CD 파이프라인에서 Firebase 크리덴셜 복원

**상태:** 채택됨  
**결정일:** 2026.07 (PR #220)

---

## 컨텍스트

Firebase Admin SDK는 서비스 계정 키 파일(`firebase-service-account.json`)을 런타임에 로드해야 한다. 이 파일은 민감 정보를 포함하므로 Git에 커밋할 수 없고, 따라서 Docker 이미지에도 포함되지 않는다.

CD 파이프라인이 Docker 이미지를 서버에 배포할 때 이 파일이 없어 서버 기동에 실패하는 문제가 발생했다.

---

## 결정

세 단계로 구성한다.

1. **저장**: `firebase-service-account.json` 내용을 base64로 인코딩하여 GitHub Secret에 저장
2. **복원**: CD 파이프라인 실행 시 SSH로 EC2에 접속하여 Secret을 디코딩·파일로 복원, 파일 존재 여부 및 JSON 유효성 검증
3. **주입**: `docker-compose.yml`에서 해당 파일을 컨테이너 내부로 read-only volume 마운트

---

## 이유

- Git에 민감 파일을 커밋하지 않고도 런타임에 필요한 파일을 서버에 안전하게 전달할 수 있는 표준 패턴이다.
- 파일 복원 직후 존재 여부와 JSON 유효성을 검증하므로, 크리덴셜 문제를 배포 초기에 감지할 수 있다.
- read-only 마운트로 컨테이너 내부에서 파일이 변조되는 것을 방지한다.

---

## 결과

- 배포 시마다 Firebase 크리덴셜이 자동 복원된다.
- `FIREBASE_SERVICE_ACCOUNT` GitHub Secret 등록 완료 (2026-07-27).
- 파일이 비어 있거나 JSON 형식이 잘못된 경우 CD가 조기 실패하여 잘못된 서버 기동을 막는다.
