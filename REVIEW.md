# 코드 리뷰 플레이북

이 문서는 **리뷰어**가 어떤 순서와 관점으로 PR을 볼지 안내한다.  
구체적인 설계 이슈와 도메인 맥락은 **[CLAUDE.md](./CLAUDE.md)** 를 함께 참고한다.

---

## 1. 즉시 차단 항목

코드가 얼마나 잘 작성됐든 아래 중 하나라도 있으면 머지하지 않는다.

- API 키·비밀번호·실 경로가 코드 또는 커밋에 포함됨
- 소유자 조건 없는 조회 — 다른 사용자의 데이터가 쿼리 단계에서 노출될 수 있다
- 상태 Enum 값 **삭제·이름 변경** 후 사용처 전수 확인 없음 — 서비스·테스트 전역에 cascade됨
- `ErrorStatus` 공통 코드·Entity 공개 메서드 수정 후 참조처 검토 없음

---

## 2. 리뷰 관점

### 2-1. 사이드 이펙트

변경한 클래스·메서드를 참조하는 다른 코드에 영향이 없는지가 핵심이다.

- `ErrorStatus` 수정 시: 해당 코드를 사용하는 서비스·테스트 전체 grep
- Entity 공개 메서드(`withdraw`, `suspend` 등) 추가·시그니처 변경 시: 전체 사용처 확인
- `OrderStatus` · `GroupBuyPostStatus` 값 변경 시: 해당 상태를 분기하는 서비스·테스트 모두 점검
- Repository 메서드 삭제 시: 서비스 전체에서 호출 여부 확인 (데드 코드로 남겨도 명시)

### 2-2. 오류 처리

- 서비스 계층에서 `BusinessException` 이외의 예외를 직접 던지지 않는가?
- `ErrorStatus`에 같은 원인의 코드가 이미 있는데 새 코드를 중복으로 추가하지 않았는가?
- HTTP 상태 코드가 의미와 일치하는가? (인증 실패 401, 권한 없음 403, 미존재 리소스 404)
- 다른 사용자 소유 리소스 접근 시 404를 반환하는가? (403은 리소스 존재 자체를 노출함)

### 2-3. API 계층

- 비즈니스 로직이 컨트롤러가 아닌 서비스에 있는가?
- 요청 DTO에 `@Valid`가 붙어 있는가? — 누락 시 Bean Validation이 실행되지 않아 잘못된 요청이 통과됨
- DTO가 Entity를 직접 노출하지 않는가?
- Swagger `@Operation` · `@ApiResponses`가 실제 동작과 일치하는가? 틀린 명세는 없는 것보다 나쁘다

### 2-4. 보안

- JWT 인증이 필요한 엔드포인트가 `SecurityConfig`의 `permitAll` 목록에 잘못 포함됐는가?
- 어드민 경로(`/admin/**`)가 일반 API FilterChain에 노출되지 않는가?
- 환경변수로 처리해야 할 값이 하드코딩됐는가?
- 외부 소셜 로그인 토큰 검증 시 `aud` 클레임을 확인하는가? — 미검증 시 다른 앱용 토큰으로 로그인 가능
- Feign 클라이언트 호출 시 `FeignException` 하위 타입(BadRequest, Unauthorized, Forbidden 등)을 구분 처리하는가?

### 2-5. 데이터베이스 · JPA

- `ddl-auto: update`에 의존하여 컬럼 추가·삭제를 검증 없이 진행하지 않는가?
  - 운영 배포 시 실패 가능성 있는 스키마 변경은 팀에 사전 공유
- N+1 문제가 생길 수 있는 연관 조회가 있는가? (`fetch join` 또는 `default_batch_fetch_size` 활용)
- 소유자 조건이 쿼리에 포함되어 있는가? (애플리케이션 레이어 필터링은 금지)

### 2-6. 테스트

- 새 상태 전환 로직에 단위 테스트가 있는가?
- 기존 테스트가 깨지지 않는가?
- `application-test.yml`에 새로 추가된 환경변수 플레이스홀더가 반영됐는가?
  - 누락 시 CI `PlaceholderResolutionException` 발생

---

## 3. 반복된 실패 패턴

새로운 사례가 생길 때마다 추가한다.

| 패턴 | 왜 문제인가 | 리뷰 시 확인할 질문 |
|---|---|---|
| `@Valid` 누락 | DTO의 Bean Validation이 실행되지 않아 잘못된 요청이 통과됨 | 잘못된 요청이 실제 API에서 400으로 처리되는가? |
| 소유권 없는 조회 후 애플리케이션에서 필터링 | 다른 사용자 데이터가 조회 단계에서 이미 노출됨 | 쿼리 자체에 소유자 조건이 포함되어 있는가? |
| `OrderStatus` · `GroupBuyPostStatus` 값 삭제 후 사용처 미확인 | 서비스·테스트 전역에 컴파일·런타임 오류 cascade | Enum 값을 참조하는 모든 분기문·테스트를 grep했는가? |
| `application-test.yml` 환경변수 누락 | CI 빌드에서 `PlaceholderResolutionException` 발생 | 새로 추가된 `${VAR}` 플레이스홀더가 test yml에도 있는가? |
| `application.yml` 키 변경 후 test yml 별도 커밋 | AuthService는 새 키를 참조하는데 test yml에는 옛 키만 있어 CI 실패 | `@Value` 키 이름을 바꿀 때 `application.yml`과 `application-test.yml`을 같은 커밋에 포함했는가? |
| 소셜 로그인 토큰 `aud` 미검증 | 다른 앱용으로 발급된 토큰으로 로그인 가능한 보안 취약점 | Google/Apple ID Token의 `aud` 클레임을 서버 클라이언트 ID와 비교하는가? |
| Entity 공개 메서드 시그니처 변경 후 서비스 미확인 | 참조처에서 컴파일 오류 또는 의도치 않은 동작 | 변경된 메서드를 호출하는 모든 서비스를 grep했는가? |
| 민감 파일을 이미지에 포함하지 않고 런타임 로드 | gitignore 파일은 Docker 이미지에 없어 기동 실패 | 런타임에 필요한 외부 파일이 CD 파이프라인에서 서버에 전달되는가? |
| cascade 없는 연관 엔티티가 있는 리소스 삭제 | DB FK 제약 위반으로 `DataIntegrityViolationException` 발생 | 삭제 대상의 `@OneToMany` 중 cascade 없는 관계가 있는가? 삭제 전 존재 여부 체크를 하는가? |
| QueryDSL implicit join — where/groupBy에서 `entity.relation.field` 사용 | explicit join이 있어도 추가 join이 생성되어 중복 조인·예상치 못한 결과 발생 | `join(groupBuyPost.artist, artist)` 후 where/groupBy에서 `artist.name`을 쓰는가? `groupBuyPost.artist.name`(implicit)이 아닌가? |
| `ApiResponse.success(status, null)` 사용 | data 없는 응답에 `"data": null`이 포함됨 — `ApiResponse.success(status)` 오버로드가 이미 존재 | 반환값이 없는 엔드포인트에서 data 없는 오버로드를 사용하는가? |
