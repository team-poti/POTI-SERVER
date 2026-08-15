# POTI 서버 프로젝트 컨텍스트

## 프로젝트 개요
연예인 굿즈 공동구매(분철) 애플리케이션 서버
- GitHub: https://github.com/team-poti/POTI-SERVER
- 서버 1인 단독 개발
- PRD: `docs/prd/PRD-POTI.md` / ADR: `docs/adr/`

## 인프라 현황
- AWS 기반, EC2 직접 배포 방식 (PROD t3.micro / DEV t3.small, RDS MySQL, S3, VPC)
- **Terraform IaC 관리 중** (`infra/` 디렉토리, 상태 파일은 `poti-terraform-state` S3 버킷) — ADR-010 참고
- 인프라 변경은 콘솔 수동 변경 금지, `.tf` 수정 → `terraform plan` → `apply` 흐름으로만
- EC2 내부 컨테이너(앱 blue/green, Redis, DEV MySQL, Dozzle)는 docker-compose로 관리
- AWS Parameter Store 환경변수 관리 검토 필요 (현재 EC2 `.env` + 로컬 `terraform.tfvars`) — #238

## 알림 시스템 아키텍처

- `Notification` 엔티티: userId(Long, FK 없음), title, body, type, deeplink, read
- `NotificationType`: `TRADE` / `EVENT`
- DB 저장은 항상 실행, FCM 발송은 유저 설정에 따라 분기
- `FcmNotificationService`는 `@Profile("!test")`로 조건부 빈 — 테스트 환경에서는 알림이 DB에 저장되지 않음
- `saveAndSend()` 패턴: DB 저장 → 설정 확인 → FCM 발송 순서
- User 테이블에 `trade_notification_enabled`, `event_notification_enabled` 컬럼 (`TINYINT(1) DEFAULT 1`)

## 소셜 로그인 구현

- **카카오**: KakaoFeignClient → Kakao API에 Access Token 전달 → 사용자 정보 조회
- **Google**: GoogleTokenFeignClient → `/tokeninfo?id_token=` 엔드포인트로 ID Token 검증 → `aud` 클레임으로 iOS/Android Client ID 일치 여부 확인
- **Apple**: ApplePublicKeyFeignClient → JWKS 조회 → kid 매칭 → RSAPublicKey 생성 → JJWT로 서명 검증 + `iss`/`aud` 검증

공통 엔드포인트: `POST /api/v1/auth/login`

환경변수 (클라이언트에서 값 수령 후 서버 `.env` 추가 필요):
- `GOOGLE_CLIENT_ID_IOS` — iOS용 Google OAuth 클라이언트 ID
- `GOOGLE_CLIENT_ID_ANDROID` — Android용 Google OAuth 클라이언트 ID
- `APPLE_BUNDLE_ID` — 앱 Bundle ID
- `APP_DEEPLINK_HOST` — 딥링크 호스트 (예: `https://app.poti.kr`)

## 핵심 이슈: 상태값 재정의

1차 MVP에서 상태값이 불명확하게 정의됨. GroupBuyPostStatus와 OrderStatus 간 연동 로직 미완성.

**GroupBuyPostStatus** (분철글 상태)
```
RECRUITING   - 모집중
CLOSED       - 모집 완료 (입금 대기)
PAYMENT_DONE - 입금 완료
SHIPPING     - 배송 시작
DELIVERED    - 배송 완료 (거래 종료)
```

**OrderStatus** (주문 상태)
```
WAIT_PAY       - 입금 대기
WAIT_PAY_CHECK - 입금 확인 대기
PAID           - 입금 완료
SHIPPED        - 배송 시작
DELIVERED      - 배송 완료
```

- 모집자/참여자 상태값 분리 유지 방향 (디자인 이미 분리됨)
- 모든 Order 입금확인 완료 시 → GroupBuyPostStatus 자동 전환 조건 미확정
- 판매자 수동 변경 시점 vs 자동 전환 시점 구분 필요

## 기술 스택 메모

- **QueryDSL**: `GroupBuyRepositoryImpl`에서 적극 사용 — `JPAQueryFactory`, Q클래스, `Projections.constructor`, `BooleanExpression`, `CaseBuilder`
- **Fulltext 검색**: MySQL ngram 기반 `MATCH AGAINST IN BOOLEAN MODE` — Native Query로 구현 (QueryDSL 미지원)
- **검색 자동완성 vs 결과**: 자동완성은 Fulltext(성능), 검색 결과는 `containsIgnoreCase` LIKE(단순성)
- **Flyway 미사용**: `ddl-auto: update` 의존 — 스키마 변경 시 팀 사전 공유 필요

## 문서 관리

### PRD (`docs/prd/PRD-POTI.md`)
- 기능 구현 완료 시 → 7절 기능 우선순위 상태를 `✅ 완료 (PR #번호)`로 변경
- 미결 사항 확정 시 → 10절에서 해당 항목 제거
- 기획 방향 변경 시 → 해당 절 수정

### ADR (`docs/adr/`) — 다음 번호: ADR-011
**새 ADR이 필요한 경우:**
- 기술 스택·라이브러리 신규 도입 또는 교체
- 인증·보안 방식 변경
- 데이터 모델의 근본적인 설계 선택
- 외부 서비스 연동 방식 결정
- 인프라 구성 방식 변경

**ADR을 쓰지 않아도 되는 경우:** 단순 기능 추가, 버그 수정, 리팩토링

작성 후 반드시 `docs/adr/README.md` 인덱스 테이블에 행 추가

## 작업 시 주의사항

- 코드 변경 시 사이드 이펙트를 반드시 확인할 것
  - 변경된 클래스/메서드를 참조하는 다른 코드에 영향이 없는지 검토
  - ErrorStatus, Entity 메서드 등 공통 코드 수정 시 전체 사용처 grep으로 확인
  - 상태값(Enum) 변경 시 해당 상태를 사용하는 서비스/테스트 모두 점검
- `@Value`로 주입하는 프로퍼티 키를 변경하면 `application.yml`과 `application-test.yml`을 **반드시 같이** 커밋할 것
  - 한쪽만 커밋하면 CI에서 `PlaceholderResolutionException`으로 빌드 실패
- QueryDSL 작성 시 explicit join한 Q객체를 where/groupBy 절에서도 일관되게 사용할 것
  - `groupBuyPost.artist.name` (implicit join) 대신 `artist.name` (explicit join 변수) 사용

## 이슈 / PR / 커밋 컨벤션

이슈와 PR을 생성할 때는 `.github/ISSUE_TEMPLATE/`, `.github/PULL_REQUEST_TEMPLATE.md`의 템플릿을 반드시 따를 것.

### 이슈 제목
```
[FEAT] 기능 이름       # 신규 기능
[FIX] 버그 내용 요약   # 버그 수정
[REFACTOR] 내용        # 리팩토링
```
- 레이블: `✨ Feat` / `🚨 Fix` / `♻️ Refactor` 등 템플릿에 정의된 값 사용

### PR 제목
```
[FEAT] #이슈번호 기능명
[FIX] #이슈번호 버그명
```
- 본문은 PR 템플릿 섹션(관련 이슈, 변경 사항, 테스트 증명, 리뷰어 참고, 체크리스트) 모두 채울 것

### 커밋 메시지
```
feat: #이슈번호 작업 내용
fix: #이슈번호 수정 내용
test: #이슈번호 테스트 내용
refact: #이슈번호 리팩토링 내용
chore: 기타 작업
```
- 타입 소문자, 콜론+공백 후 `#이슈번호` 명시
- Co-Authored-By 줄 추가하지 않음
