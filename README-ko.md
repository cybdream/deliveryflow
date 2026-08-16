# DeliveryFlow

[![CI](https://github.com/cybdream/deliveryflow/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/cybdream/deliveryflow/actions/workflows/ci.yml)

[English](README-en.md)

배송 접수부터 기사 배정, 배송 상태 관리, 완료 이력 조회까지 지원하는 배송 운영 관리 API 프로젝트입니다.

과거 배송 접수 시스템 개발 경험을 바탕으로, 운영 환경에서 중요한 상태 전이 검증, 역할별 권한 관리, 변경 이력 보관을 Spring Boot 기반으로 재구성합니다.

## 배포 및 API 문서

- 배포 API: [https://deliveryflow-production.up.railway.app](https://deliveryflow-production.up.railway.app)
- Swagger UI: [https://deliveryflow-production.up.railway.app/swagger-ui/index.html](https://deliveryflow-production.up.railway.app/swagger-ui/index.html)
- 상태 확인: [https://deliveryflow-production.up.railway.app/api/v1/health](https://deliveryflow-production.up.railway.app/api/v1/health)

Railway 운영 환경에서 관리자 로그인, 주문 등록, 주문 조회까지 외부 접속으로 검증했습니다.
## 데모 계정

| 이메일 | 역할 | 비밀번호 |
|---|---|---|
| `admin@deliveryflow.local` | `ADMIN` | 공개하지 않음 |
| `driver@deliveryflow.local` | `DRIVER` | 공개하지 않음 |

배포된 API의 데이터 변경을 방지하기 위해 비밀번호는 README에 게시하지 않습니다. 테스트 접근 정보는 요청 시 별도로 제공합니다.
## 프로젝트 목표

- 주문 접수와 배송 기사 배정 업무를 API로 구현합니다.
- 잘못된 배송 상태 변경을 막고 모든 변경 이력을 남깁니다.
- 관리자와 배송 기사의 접근 권한을 구분합니다.
- 테스트, API 문서화, Docker, CI/CD까지 적용해 운영 가능한 백엔드 서비스를 만듭니다.

## 주요 기능

- 주문 등록 및 조회
- 배송 기사 배정 및 재배정
- 배송 상태 변경
- 배송 이력 및 운영 메모 관리
- 관리자 배송 현황 조회
- 고객 배송 조회

## 배송 상태 흐름

```mermaid
flowchart LR
    A[RECEIVED<br/>주문 접수] --> B[ASSIGNED<br/>기사 배정]
    B --> C[IN_DELIVERY<br/>배송 시작]
    C --> D[DELIVERED<br/>배송 완료]
    B --> E[ON_HOLD<br/>배송 보류]
    C --> E
    E --> B
    E --> C
    A --> F[CANCELLED<br/>배송 취소]
    B --> F
    E --> F
```

`DELIVERED`와 `CANCELLED` 상태는 최종 상태이므로 이후 변경할 수 없습니다.

## 기술 스택

| 구분 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.1 |
| Database | PostgreSQL |
| Data Access | Spring Data JPA |
| Security | Spring Security, JWT |
| API Documentation | Swagger / OpenAPI |
| Test | JUnit 5, Mockito |
| Deployment | Docker, GitHub Actions, Railway |

## 개발 계획

| 단계 | 내용 | 상태 |
|---|---|---|
| 1 | 프로젝트 설정 및 주문 등록·조회 API | 완료 |
| 2 | 배송 기사 등록·조회 및 기사 배정 | 완료 |
| 3 | 배송 상태 변경 및 배송 이력 | 완료 |
| 4 | JWT 로그인 | 완료 |
| 5 | 요청 인증·역할 권한 관리 | 완료 |
| 6 | 배송 목록·검색 | 완료 |
| 7 | 표준 오류 응답 | 완료 |
| 8 | 운영 대시보드 | 완료 |
| 9 | Docker와 Swagger API 문서화 | 완료 |
| 10 | 통합 테스트와 GitHub Actions CI | 완료 |
| 11 | Railway 운영 배포 설정 | 완료 |
| 5 | 테스트·Docker·CI/CD 배포 | 진행 예정 |

## 문서

- [프로젝트 한글 명세서](docs/deliveryflow-portfolio-spec-ko.md)
- [Project Specification (English)](docs/deliveryflow-portfolio-spec-en.md)
- [현재 상태와 다음 작업](docs/00-current-status-ko.md)
- [개발 여정 - Markdown](docs/deliveryflow-development-journey-ko.md)
- [개발 여정 - PDF](docs/deliveryflow-development-journey-ko-v2.pdf)
- [구현 현황](docs/implementation-status-ko.md)
- [로그인 API](docs/login-api-ko.md)
- [JWT 인증과 역할 권한](docs/jwt-authorization-ko.md)
- [배송 목록 및 검색 API](docs/delivery-list-api-ko.md)
- [표준 오류 응답](docs/error-response-ko.md)
- [오류 메시지 언어 선택](docs/error-message-language-ko.md)
- [운영 대시보드 API](docs/dashboard-api-ko.md)
- [Swagger와 Docker 실행](docs/swagger-and-docker-ko.md)
- [GitHub Actions 자동 테스트](docs/github-actions-ci-ko.md)
- [Railway 배포 안내](docs/railway-deployment-ko.md)
- [Railway 외부 검증 체크리스트](docs/railway-external-verification-ko.md)
- [PowerShell 통합 테스트](docs/powershell-integration-test-ko.md)
- [로컬 계정 비밀번호 재설정](docs/local-account-password-reset-ko.md)
- [포트폴리오 소개](docs/portfolio-summary-ko.md)

## 실행 방법

현재 주문 및 배송 기사 API를 실행할 수 있습니다. PostgreSQL 연결 정보는 Git에서 제외된 `application-local.properties`에 설정합니다.

```bash
./gradlew bootRun
```

## 프로젝트 구조

```text
src/main/java/com/deliveryflow
├── auth          # 인증과 인가
├── user          # 사용자와 배송 기사
├── order         # 주문 접수
├── delivery      # 배송, 상태 변경, 이력
└── common        # 공통 설정, 예외, 응답 형식
```

## 핵심 설계 원칙

- 배송 기사는 자신에게 배정된 배송만 조회하고 수정할 수 있습니다.
- 상태 변경 시 이전 상태, 변경 상태, 변경자, 변경 시각, 사유를 이력으로 저장합니다.
- 보류 및 취소 시에는 사유를 반드시 입력해야 합니다.
- 동시 수정으로 인한 데이터 충돌을 줄이기 위해 낙관적 락을 적용합니다.

## 향후 개선 계획

- 배송 기사별 당일 업무량 통계
- 배송 실패 사유 분석
- 알림 기능
- 고객용 배송 조회 화면
- 배포 환경 모니터링 및 로그 관리
