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
| 배송 현황 대시보드 | `GET` | `/api/v1/dashboard/delivery-status` |

## 상태 변경 예시

```text
PATCH /api/v1/deliveries/1/status?status=IN_DELIVERY
```

보류 또는 취소할 때는 `reason`을 반드시 입력합니다.

```text
PATCH /api/v1/deliveries/1/status?status=ON_HOLD&reason=수령인%20부재
```

처리자 정보는 JWT의 로그인 이메일에서 자동으로 기록됩니다. 기사 계정은 본인에게 배정된 배송의 `IN_DELIVERY`, `DELIVERED`, `ON_HOLD` 상태만 변경할 수 있습니다.

## 검증된 규칙

- `ASSIGNED → IN_DELIVERY → DELIVERED` 상태 전이를 지원합니다.
- 보류(`ON_HOLD`)와 취소(`CANCELLED`)에는 사유가 필요합니다.
- 완료 또는 취소된 배송은 이후 상태를 바꿀 수 없습니다.
- 배정 및 상태 변경마다 `delivery_histories`에 이력이 저장됩니다.
