# 운영 대시보드 API

관리자가 선택한 배송 예정일의 전체 배송 수와 상태별 건수를 조회합니다.

```http
GET /api/v1/dashboard/delivery-status?scheduledDate=2026-08-13
Authorization: Bearer {adminToken}
```

`scheduledDate`를 생략하면 모든 예정일의 배송을 합산합니다.

## 응답 예시

```json
{
  "scheduledDate": "2026-08-13",
  "totalCount": 3,
  "statusCounts": {
    "ASSIGNED": 1,
    "IN_DELIVERY": 1,
    "ON_HOLD": 0,
    "DELIVERED": 1,
    "CANCELLED": 0
  }
}
```

상태별 건수는 해당 상태의 배송이 없어도 `0`으로 반환하므로 화면에서 바로 사용할 수 있습니다.

## PowerShell

```powershell
Invoke-RestMethod `
  "http://localhost:8080/api/v1/dashboard/delivery-status?scheduledDate=2026-08-13" `
  -Headers $headers
```
