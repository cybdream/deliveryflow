# DeliveryFlow 현재 상태와 다음 작업

마지막 정리일: 2026-08-16

이 문서는 긴 작업 대화 대신, 다음 개발을 시작할 때 가장 먼저 확인하는 요약 문서입니다.

## 프로젝트 한눈에 보기

- 목적: 배송 접수, 기사 배정, 배송 상태 관리와 이력 조회를 제공하는 백엔드 API
- 기술: Java 17, Spring Boot, Spring Data JPA, PostgreSQL, Spring Security/JWT, Swagger, Docker, GitHub Actions, Railway
- 소스 저장소: `https://github.com/cybdream/deliveryflow`
- 배포 API: `https://deliveryflow-production.up.railway.app`
- Swagger UI: `https://deliveryflow-production.up.railway.app/swagger-ui/index.html`
- 상태 확인: `GET /api/v1/health`

## 완료된 핵심 흐름

1. 관리자 로그인 및 JWT 발급
2. 주문 등록과 목록 조회
3. 배송 기사 등록과 목록 조회
4. 주문에 기사 배정
5. 기사가 자신의 배정 목록 조회
6. 기사가 배송 상태 변경
7. 관리자가 배송 이력과 당일 배송 현황 확인

외부 Railway 환경에서 관리자 로그인부터 주문 등록, 기사 배정, 기사 상태 변경, 배송 이력과 대시보드 조회까지 전체 운영 흐름을 Swagger와 PowerShell 통합 테스트로 확인했습니다.

## 역할과 권한

| 역할 | 할 수 있는 일 |
|---|---|
| `ADMIN` | 주문·기사·배송 관리, 배송 배정, 모든 상태 변경, 이력·대시보드 조회 |
| `DRIVER` | 자신에게 배정된 배송 목록 조회, `IN_DELIVERY`·`DELIVERED`·`ON_HOLD`로 상태 변경 |

기사는 `ASSIGNED`나 `CANCELLED` 상태로 직접 변경할 수 없습니다. 보류와 취소에는 `reason`이 필요합니다.

## 주요 API

| 기능 | 메서드 | 경로 |
|---|---|---|
| 로그인 | `POST` | `/api/v1/auth/login` |
| 주문 등록·조회 | `POST`, `GET` | `/api/v1/orders` |
| 기사 등록·조회 | `POST`, `GET` | `/api/v1/drivers` |
| 배송 배정·전체 조회 | `POST`, `GET` | `/api/v1/deliveries` |
| 내 배송 목록 | `GET` | `/api/v1/deliveries/me` |
| 배송 상태 변경 | `PATCH` | `/api/v1/deliveries/{deliveryId}/status?status=IN_DELIVERY` |
| 배송 이력 | `GET` | `/api/v1/deliveries/{deliveryId}/histories` |
| 배송 현황 | `GET` | `/api/v1/dashboard/delivery-status` |

상태 변경 API는 Swagger에서 `status`를 선택해서 실행합니다. `reason`은 `ON_HOLD`, `CANCELLED`일 때만 입력합니다.

## 로컬 실행

1. PostgreSQL을 실행하고 로컬 데이터베이스를 준비합니다.
2. Git에서 제외된 `src/main/resources/application-local.properties`에 데이터베이스와 로컬 계정 비밀번호를 설정합니다.
3. 프로젝트 폴더에서 애플리케이션을 실행합니다.

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=local'
```

Swagger는 `http://localhost:8080/swagger-ui/index.html`에서 열 수 있습니다.

Docker로 실행할 때는 `.env`에 필요한 로컬 값을 설정한 후 다음을 실행합니다.

```powershell
docker compose up --build
```

## 배포 환경 주의 사항

- Railway 앱 서비스와 PostgreSQL 서비스는 같은 Railway 프로젝트 안에 둡니다.
- 앱의 데이터베이스 변수는 PostgreSQL 서비스의 내부 연결 변수(`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`)를 참조합니다.
- JWT 비밀값과 데이터베이스 비밀번호는 GitHub, README, Swagger 예시, 채팅에 기록하지 않습니다.
- Railway 환경 변수 변경 후에는 재배포 또는 재시작이 필요합니다.

## 다음에 할 일

1. 고객 배송 조회 API를 로컬 Swagger와 Railway 배포 환경에서 검증합니다.
2. 검증 결과와 Swagger 화면을 포트폴리오 증빙 자료로 정리합니다.
3. 다음 기능 중 하나를 선택합니다.
   - 기사별 당일 배송 건수와 완료율 통계
   - 테스트 케이스 확장 및 코드 구조 리팩터링

## 관련 문서

- [구현 현황](implementation-status-ko.md)
- [Railway 외부 검증 체크리스트](railway-external-verification-ko.md)
- [Railway 배포 안내](railway-deployment-ko.md)
- [Swagger와 Docker 실행](swagger-and-docker-ko.md)
- [JWT 인증과 역할 권한](jwt-authorization-ko.md)
- [포트폴리오 소개](portfolio-summary-ko.md)
