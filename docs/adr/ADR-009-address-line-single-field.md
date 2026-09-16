# ADR-009: 배송지 주소 필드 구조 — 단일 addressLine 유지

**상태:** 채택됨  
**결정일:** 2026.08.02

---

## 컨텍스트

배송지 입력 화면에서 카카오 우편번호 서비스 API를 도입함에 따라, 클라이언트가 다음 3가지 주소 데이터를 획득하게 되었다.

- `zonecode`: 국가기초구역번호 (우편번호)
- `roadAddress`: 기본 도로명 주소 (카카오 API 반환)
- `detailAddress`: 사용자가 직접 입력하는 상세주소 (동/호수 등)

이에 따라 서버 API 스펙을 어떻게 정할지 결정이 필요했다.

**선택지**
1. **기존 스펙 유지**: 클라이언트가 `roadAddress + detailAddress`를 합쳐 기존 `addressLine` 필드로 전달
2. **스펙 분리**: `roadAddress`, `detailAddress` 두 필드로 나눠 전달 → 서버 DTO, 엔티티, DB 스키마 변경 필요

---

## 결정

**Option 1 — 기존 `addressLine` 단일 필드 유지**를 채택한다.

클라이언트 매핑 규칙:

```
zipcode     = zonecode
addressLine = detailAddress가 있으면: roadAddress + " " + detailAddress
              detailAddress가 없으면: roadAddress
```

예시:
- 상세주소 있음: `"서울 강남구 테헤란로 1 101동 202호"`
- 상세주소 없음: `"서울 강남구 테헤란로 1"`

---

## 이유

- `zipcode`는 이미 별도 필드로 분리되어 있어 우편번호 저장에 문제없다.
- 판매자가 참여자 배송지를 조회하는 현재 사용처는 단순 텍스트 표시가 목적이므로 합산 문자열로 충분하다.
- 현재 배송사 API 연동·주소 파싱·자동 검증 기능이 없어 필드 분리로 얻는 실익이 없다.
- 서버 DTO·엔티티·DB 스키마 변경 및 마이그레이션 비용을 줄인다.

---

## 결과

- 서버 코드 변경 없이 즉시 적용 가능하다.
- 향후 택배사 API 연동, 주소 파싱, 검색 기능이 필요해질 경우 `addressLine`을 `roadAddress` + `detailAddress`로 분리하는 스키마 마이그레이션이 필요하다. 그 시점에 이 결정을 재검토한다.
