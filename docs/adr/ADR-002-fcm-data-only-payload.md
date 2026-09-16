# ADR-002: FCM data-only payload 선택

**상태:** 채택됨  
**결정일:** 2026.07

---

## 컨텍스트

알림(Phase 2 MUST)을 구현할 때 FCM 메시지 payload 방식을 선택해야 했다. FCM은 크게 두 가지를 지원한다.

- `notification` 필드: OS가 직접 알림을 표시. 앱이 백그라운드일 때 딥링크 처리 불가.
- `data` 필드만: 앱이 직접 메시지를 수신·파싱. 딥링크 처리 가능.

포티의 알림은 단순 노출이 아니라 **특정 화면으로 이동**하는 딥링크를 포함해야 한다.

---

## 결정

`notification` 필드 없이 `data` 필드만 사용하는 **data-only payload**를 채택한다.

payload 포함 필드:

- `title`: 알림 제목
- `body`: 알림 본문
- `deeplink`: 이동 대상 화면 경로 (`{APP_DEEPLINK_HOST}/{path}` 형태, dev/prod 환경변수로 분리)

딥링크 경로 (클라이언트 팀 확정, PR #223):

| 화면 | 경로 | 수신자 |
|------|------|--------|
| 분철글 상세 | `/pot/{postId}` | - |
| 모집자 상세 내역 | `/recruiter-detail/{postId}` | 모집자 |
| 모집자 참여자 관리 | `/participant-manage/{postId}` | 모집자 |
| 참여자 상세 내역 | `/participant-detail/{orderId}` | 참여자 |

---

## 이유

- `notification` 필드를 포함하면 앱이 백그라운드 상태일 때 OS가 알림을 가로채 표시하므로, 앱이 딥링크를 직접 제어할 수 없다.
- data-only는 앱 foreground·background 모두에서 앱이 직접 수신·파싱하므로 딥링크 처리가 가능하다.
- iOS·Android 양쪽에서 일관된 처리 방식을 유지할 수 있다.

---

## 결과

- 딥링크는 `app.deeplink.host` 환경변수로 dev/prod 분리하여 관리.
- FCM 발송 실패는 비즈니스 트랜잭션에 영향을 주지 않는다 (로그만 기록).
- 구현된 알림 6종:
  - 새 참여자 참여 → 모집자
  - 입금 확인 요청 → 모집자
  - 배송 시작 요청(모든 참여자 입금 완료) → 모집자
  - 분철글 상태 변경(CLOSED, PAYMENT_DONE) → 모집자 + 참여자 전원
  - 배송 시작 → 해당 참여자 개별 발송 (#265: Order 단위로 변경)
  - 분철 전체 완료(모든 수령 확인) → 모집자
- FCM 발송과 비즈니스 트랜잭션 분리(AFTER_COMMIT 패턴): PR #258 적용 완료.
