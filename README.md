<img width="12144" height="5400" alt="img_poti_insta" src="https://github.com/user-attachments/assets/258a0255-2927-494c-a41e-f16a97e43377" />


## 🌠 포티(Poti) 
**원하는 굿즈만 쏙 덕후들을 하나로 잇는 공간**

</br>

## 🔢 목차
[프로젝트 설명](#프로젝트-설명)</br>
[서비스 목표](#서비스-목표)</br>
[주요 기능](#주요-기능)</br>
[아키텍쳐](#시스템-아키텍쳐)</br>
[기술 및 아키텍쳐 선정](#기술-및-아키텍쳐-선정)</br>
[컨벤션 규칙 및 브랜치 전략](#컨벤션-규칙-및-전략)</br>
[팀원별 역할 분담](#팀원별-역할-분담)</br>
[폴더링](#폴더링)</br>

</br>

## 🎥 프로젝트 설명 
**포티**는 원하는 멤버만 선택해 구매하고 싶은 덕후를 위한, 맞춤형 굿즈 공동구매 서비스입니다.

</br>

## 🎤 서비스 목표
- 흩어진 분철 정보와 불투명한 참여 현황을 필터 기능과 거래 상태바로 정리해, 원하는 분철을 빠르고 투명하게 참여할 수 있도록 돕는 서비스

</br>

## 📍 주요 기능 
1. 최애 기반 온보딩 : `최애 아티스트`를 수집합니다. 사용자의 최애 아티스트가 반영된 추천 컨텐츠를 노출시킵니다.</br>
</br>

2. 홈 : `추천 알고리즘`에 기반해 사용자의 최애에 기반한 굿즈를 추천받고, 하루마다 바뀌는 `랜덤 굿즈`를 확인합니다. </br>
</br>

3. 굿즈 리스트 : 원하는 굿즈의 분철팟만 깔끔하게 모아보고, 자신의 기준에 맞춰 `필터링된 분철팟`을 탐색합니다.</br>
</br>

4. 분철팟 상세 : 분철팟에 대한 상세 정보를 확인합니다. 또 모집자의 모집 내역과 리뷰 등의 정보를 확인해 `신뢰도를 제고하여 안전한 거래를 가능`하게 만듭니다.</br>
</br>

5. 분철 참여 : `간단한 정보` 입력만으로 원하는 분철에 참여합니다.</br>
</br>

6. 분철팟 등록 : `표준화된 등록 과정`으로 고민없이 분철팟 등록이 가능합니다.</br>
</br>

</br>

## ⚙️ 시스템 아키텍쳐
<img width="1102" height="825" alt="image" src="https://github.com/user-attachments/assets/027e85b4-34bd-46e5-9126-1739dc292246" />

</br>

</br>

## ⚙️ 기술 및 아키텍처 선정
+ `Backend Core`</br>
+ `Language - Java 21 (LTS)`</br>
+ `Framework - Spring Boot 3.5.9`</br>
+ `Data & Persistence`</br>
+ `Database - MySQL 8.4.3`</br>
+ `ORM - Spring Data JPA`</br>
+ `Query - QueryDSL 5.0`</br>
+ `Infrastructure & Performance`</br>
+ `Cache - Redis`</br>
+ `Storage - AWS S3 (Presigned URL)`</br>
+ `Web Server - Nginx`</br>
+ `CI/CD - Docker Compose, GitHub Actions`</br>
+ `Image Build - Jib`</br>
+ `Monitoring - Dozzle, Prometheus, Grafana Cloud`</br>
+ `Error Tracking - Sentry`</br>
+ `API Docs - Swagger (SpringDoc)`</br>
+ `External Service`</br>
+ `Auth - Kakao / Google / Apple OAuth`</br>
+ `Push Notification - Firebase Cloud Messaging (FCM)`</br>
+ `Analytics - Mixpanel`</br>
+ `AI - Google Gemini API`</br>
+ `Alert - Discord Webhook`</br>
</br>


## ❗ 컨벤션 규칙 및 전략

**컨벤션:**  [Convention](https://fir-custard-683.notion.site/2f0909fac41b81518965dc6ba163eaa0?source=copy_link) </br>
**협업 전략 & 그라운드 룰:**  [Branch Strategy](https://fir-custard-683.notion.site/Ground-Rule-2f0909fac41b81b3874ff77034eba691?source=copy_link) </br></br>



## 👤 팀원별 역할 분담
|[![임채륜](https://github.com/user-attachments/assets/c5fb5c3e-1185-4020-86a7-87f9e3375a4a)](https://github.com/PBEM22)|[![박시현](https://github.com/user-attachments/assets/9f54d4fe-7ee6-4e7b-b048-0008f91a4abb)](https://github.com/88guri)|
|:---------:|:---------:|
|[임채륜](https://github.com/PBEM22)|[박시현](https://github.com/88guri)|
|`분철` `관리자` `기타`|`유저` `참여자` `후기`|

</br>
</br>

## 🗂️ 폴더링
DDD의 핵심 철학인 Bounded Context를 반영하여 도메인형 패키지 구조를 채택했습니다.</br>
```bash
src/main/java/org/sopt/poti
├── 📂 global                  # 공통 커널 & 횡단 관심사
│   ├── config                 # 설정 (Security, QueryDSL, Redis, Async)
│   ├── common                 # 공통 응답/유틸 (ApiResponse, BaseEntity)
│   ├── error                  # 예외 처리 (GlobalExceptionHandler)
│   ├── external               # 인프라 계층 (Infrastructure Layer)
│   │   ├── s3                 # AWS S3 구현체
│   │   ├── discord            # Discord Webhook 구현체
│   │   ├── kakao              # Kakao OAuth 구현체
│   │   ├── google             # Google OAuth 구현체
│   │   ├── apple              # Apple ID 검증 구현체
│   │   └── mixpanel           # Mixpanel 이벤트 트래킹 구현체
│   ├── scheduler              # 스케줄 작업
│   ├── util                   # 공통 유틸리티
│   └── security               # 인증/인가 (JwtFilter, UserPrincipal)
│
├── 📂 domain                  # 핵심 비즈니스 로직
│   ├── 📂 user                # [회원] 도메인 컨텍스트
│   ├── 📂 auth                # [인증] 도메인 컨텍스트 (Kakao·Google·Apple OAuth)
│   ├── 📂 artist              # [아티스트] 도메인 컨텍스트
│   ├── 📂 groupbuy            # [분철 모집] 도메인 컨텍스트
│   ├── 📂 order               # [주문] 도메인 컨텍스트
│   ├── 📂 delivery            # [배송] 도메인 컨텍스트
│   ├── 📂 participation       # [참여] 도메인 컨텍스트
│   ├── 📂 payment             # [결제] 도메인 컨텍스트
│   ├── 📂 notification        # [알림] 도메인 컨텍스트
│   ├── 📂 fcmtoken            # [FCM 토큰] 도메인 컨텍스트
│   ├── 📂 item                # [굿즈 아이템] 도메인 컨텍스트
│   ├── 📂 feed                # [피드] 도메인 컨텍스트
│   ├── 📂 home                # [홈] 도메인 컨텍스트
│   ├── 📂 search              # [검색] 도메인 컨텍스트
│   ├── 📂 review              # [리뷰] 도메인 컨텍스트
│   ├── 📂 banner              # [배너] 도메인 컨텍스트
│   ├── 📂 image               # [이미지] 도메인 컨텍스트
│   └── 📂 admin               # [관리] 도메인 컨텍스트
│
└── PotiApplication.java       
```
