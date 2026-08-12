# ADR-010: Terraform 기반 IaC 도입 및 기존 인프라 import

**상태:** 채택됨  
**결정일:** 2026.08

---

## 컨텍스트

1차 MVP 인프라는 AWS 콘솔에서 수동으로 구성했다. 2차 개발부터 서버 1인 체제가 되면서 다음 문제가 커졌다.

- 인프라 구성이 콘솔 클릭 이력으로만 존재해 재구성·복구가 불가능에 가까움
- 변경 이력이 추적되지 않아 "왜 이 보안그룹이 있지?"에 답할 수 없음
- 운영/개발 환경을 새로 구성할 때마다 수동 반복 작업 발생

관리 대상: EC2 2대(PROD/DEV), RDS(MySQL), S3, VPC/서브넷/보안그룹.

---

## 결정

- **Terraform**으로 AWS 리소스를 코드 관리한다. 코드는 앱 레포의 `infra/` 디렉토리에 둔다 (1인 운영 규모에서 별도 레포는 과함).
- 상태 파일은 **S3 백엔드**(`poti-terraform-state` 버킷)에 저장한다.
- 기존 리소스는 삭제 없이 `terraform import`로 편입하고, `terraform plan`이 **No changes**가 될 때까지 코드를 실제 설정에 맞춘다.
- 예외로 RDS는 마스터 패스워드 분실로 삭제 후 Terraform으로 재생성했다 (덤프 백업 보유, 엔드포인트 동일).
- EC2 내부 컨테이너(앱, Redis, DEV MySQL, Dozzle)는 Terraform 대상이 아니며 기존 docker-compose 파일로 관리를 유지한다.
- DB 패스워드는 `terraform.tfvars`(gitignore)로 분리한다. 추후 Parameter Store 전환 검토.

---

## 이유

- Terraform은 AWS 지원이 가장 성숙하고 레퍼런스가 많아 1인 운영에서 학습·유지 비용이 가장 낮다.
- import 방식은 기존 리소스를 건드리지 않아 서비스 영향 없이 전환 가능하다.
- 상태 파일을 S3에 두면 로컬 분실 위험이 없고, 이후 팀 확장 시에도 그대로 쓸 수 있다.
- 앱/인프라 레포 통합은 PR에 인프라 변경이 섞이는 단점이 있으나, 리소스 수가 적은 현재는 단순함이 우선이다.

---

## 결과

- `terraform plan` 기준 코드와 실제 인프라 완전 일치 (No changes).
- 이후 인프라 변경은 콘솔 수동 변경 금지, `.tf` 수정 → `plan` → `apply` 흐름으로만 진행한다.
- import 과정에서 얻은 교훈: 보안그룹 `description` 불일치는 재생성(destroy)을 유발하므로 plan의 `forces replacement` 표시를 반드시 확인해야 한다. EC2 `user_data`처럼 코드로 관리하지 않을 속성은 `lifecycle.ignore_changes`로 선언한다.
- 한계: 인프라가 두 레이어(Terraform + docker-compose)로 나뉘며, EC2 재생성 시 컨테이너 기동은 아직 수동이다. EC2 재생성이 필요해지는 시점에 user_data 스크립트로 자동화한다.
