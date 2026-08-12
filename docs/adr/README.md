# ADR — POTI 서버

Architecture Decision Records. 설계 결정의 컨텍스트·결정 내용·이유·결과를 추적합니다.

| # | 제목 | 상태 | 결정일 |
|---|------|------|--------|
| [ADR-001](ADR-001-status-machine-separation.md) | 상태 머신 분리 설계 (GroupBuyPostStatus / OrderStatus) | 채택됨 | 2026.01 |
| [ADR-002](ADR-002-fcm-data-only-payload.md) | FCM data-only payload 선택 | 채택됨 | 2026.07 |
| [ADR-003](ADR-003-fcm-optional-bean-injection.md) | FCM 선택적 빈 주입 (@Autowired required=false + @Profile) | 채택됨 | 2026.07 |
| [ADR-004](ADR-004-optimistic-lock.md) | Order / Payment 낙관적 잠금 (@Version) | 채택됨 | 2026.01 |
| [ADR-005](ADR-005-user-soft-delete.md) | User 소프트 삭제 | 채택됨 | 2026.01 |
| [ADR-006](ADR-006-dual-security-filter-chain.md) | 이중 Spring Security 필터체인 (어드민 vs API) | 채택됨 | 2026.07 |
| [ADR-007](ADR-007-firebase-credentials-cd.md) | CD 파이프라인에서 Firebase 크리덴셜 복원 | 채택됨 | 2026.07 |
| [ADR-008](ADR-008-expired-fcm-token-auto-delete.md) | 만료 FCM 토큰 자동 삭제 | 채택됨 | 2026.07 |
| [ADR-009](ADR-009-address-line-single-field.md) | 배송지 주소 필드 구조 — 단일 addressLine 유지 | 채택됨 | 2026.08 |
| [ADR-010](ADR-010-terraform-iac.md) | Terraform 기반 IaC 도입 및 기존 인프라 import | 채택됨 | 2026.08 |
