# Railway 외부 검증 체크리스트

Swagger UI에서 실제 Railway 배포 환경을 검증하는 절차입니다.

- Swagger UI: `https://deliveryflow-production.up.railway.app/swagger-ui/index.html`
- 비밀번호와 JWT는 화면 공유, 문서, GitHub에 저장하지 않습니다.

## 1. 상태 확인

`GET /api/v1/health`를 실행합니다.

```json
{"status":"UP"}
```

응답을 확인하면 배포된 앱이 외부 요청을 받고 있음을 의미합니다.

## 2. 관리자 주문 흐름

1. `POST /api/v1/auth/login`으로 관리자 계정에 로그인합니다.
2. 응답의 `accessToken`으로 Swagger **Authorize**를 수행합니다.
3. `POST /api/v1/orders`로 주문을 등록합니다.
4. `GET /api/v1/orders`에서 `sort=createdAt,desc`로 방금 등록한 주문을 확인합니다.

## 3. 배송 배정 흐름

관리자 인증 상태에서 진행합니다.

1. `GET /api/v1/drivers`로 기사 ID를 확인합니다.
2. 주문 조회 응답에서 주문 ID를 확인합니다.
3. `POST /api/v1/deliveries`를 실행합니다.

```json
{
  "orderId": 1,
  "driverId": 2,
  "scheduledDate": "2030-01-01"
}
```

응답의 배송 ID를 기록합니다. 실제 테스트에서는 조회 결과의 ID로 바꿉니다.

## 4. 기사 배송 상태 변경

1. 관리자 토큰을 해제하거나 새로고침한 뒤, 기사 계정으로 로그인합니다.
2. 기사 토큰으로 Swagger **Authorize**를 수행합니다.
3. `GET /api/v1/deliveries/me`로 배정된 배송을 확인합니다.
4. `PATCH /api/v1/deliveries/{deliveryId}/status`를 실행합니다.
5. Swagger의 `status` 선택 목록에서 `IN_DELIVERY`를 고릅니다. `reason`은 비워 둡니다.

## 5. 관리자 최종 확인

관리자 계정으로 다시 로그인한 뒤 다음을 확인합니다.

- `GET /api/v1/deliveries/{deliveryId}/histories`: `ASSIGNED`, `STATUS_CHANGED` 이력
- `GET /api/v1/dashboard/delivery-status`: 당일 상태 집계

## 검증 결과

2026-08-16에 Railway 배포 환경에서 다음 흐름을 모두 성공적으로 확인했습니다.

- 공개 상태 확인 API와 Swagger UI 접근
- 관리자 로그인, 주문 등록과 조회
- 기사 배정
- 기사 로그인, 본인 배송 목록 조회
- `IN_DELIVERY`와 `DELIVERED` 상태 변경
- 관리자 배송 이력과 당일 배송 현황 조회
- PowerShell 통합 테스트 스크립트로 같은 전체 흐름 재검증

테스트 데이터의 식별값, 비밀번호, JWT는 재사용되지 않는 운영 데이터이므로 문서에 기록하지 않습니다.

## 완료 기준

주문 등록 → 기사 배정 → 기사 상태 변경 → 이력 및 대시보드 확인까지 모두 성공하면 핵심 운영 흐름의 외부 검증이 완료됩니다.
