# 배송 목록 및 검색 API

## 관리자: 전체 배송 목록

```http
GET /api/v1/deliveries
Authorization: Bearer {adminToken}
```

선택 조건을 조합할 수 있습니다.

```text
?status=IN_DELIVERY
?driverId=3
?scheduledDate=2026-08-13
?status=ASSIGNED&scheduledDate=2026-08-13&page=0&size=10
```

## 배송 기사: 내 배송 목록

```http
GET /api/v1/deliveries/me
Authorization: Bearer {driverToken}
```

기사 계정은 토큰의 이메일을 기준으로 본인에게 배정된 배송만 조회합니다.

```text
?status=ASSIGNED
?scheduledDate=2026-08-13
?page=0&size=10
```

## PowerShell 예시

```powershell
# 관리자: 오늘 배송 중인 전체 배송
Invoke-RestMethod "http://localhost:8080/api/v1/deliveries?status=IN_DELIVERY&scheduledDate=2026-08-13" -Headers $headers

# 배송 기사: 내 배송 목록
Invoke-RestMethod "http://localhost:8080/api/v1/deliveries/me" -Headers $driverHeaders
```

관리자는 `/api/v1/deliveries`를 사용하며, 배송 기사는 `/api/v1/deliveries/me`만 사용할 수 있습니다.
