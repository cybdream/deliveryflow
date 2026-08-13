# 구현 현황

## 완료된 API

| 기능 | 메서드 | 경로 |
|---|---|---|
| 주문 등록 | `POST` | `/api/v1/orders` |
| 주문 목록 조회 | `GET` | `/api/v1/orders` |
| 배송 기사 등록 | `POST` | `/api/v1/drivers` |
| 배송 기사 목록 조회 | `GET` | `/api/v1/drivers` |
| 배송 배정 | `POST` | `/api/v1/deliveries` |
| 배송 상태 변경 | `PATCH` | `/api/v1/deliveries/{deliveryId}/status` |
| 배송 이력 조회 | `GET` | `/api/v1/deliveries/{deliveryId}/histories` |
| 로그인 및 JWT 발급 | `POST` | `/api/v1/auth/login` |
| JWT 인증 및 역할 권한 | - | 보호된 API 전체 |
| 전체 배송 목록·검색 | `GET` | `/api/v1/deliveries` |
| 내 배송 목록·검색 | `GET` | `/api/v1/deliveries/me` |

## 상태 변경 예시

```json
PATCH /api/v1/deliveries/1/status
{
  "status": "IN_DELIVERY",
  "changedBy": "홍길동"
}
```

보류 또는 취소할 때는 `reason`을 반드시 입력합니다.

```json
{
  "status": "ON_HOLD",
  "reason": "수령인 부재로 저녁 배송 요청",
  "changedBy": "홍길동"
}
```

현재는 로그인 기능 전 단계이므로 `changedBy`에 처리자 이름을 직접 전송합니다. 인증 기능을 구현하면 로그인한 사용자 정보로 바꿉니다.

## 검증된 규칙

- `ASSIGNED → IN_DELIVERY → DELIVERED` 상태 전이를 지원합니다.
- 보류(`ON_HOLD`)와 취소(`CANCELLED`)에는 사유가 필요합니다.
- 완료 또는 취소된 배송은 이후 상태를 바꿀 수 없습니다.
- 배정 및 상태 변경마다 `delivery_histories`에 이력이 저장됩니다.

