# JWT 인증과 역할 권한

## 토큰으로 요청하기

로그인 후 받은 토큰을 PowerShell 변수에 보관합니다.

```powershell
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/auth/login" -ContentType "application/json" -Body '{"email":"admin@deliveryflow.local","password":"YOUR_PASSWORD"}'
$token = $login.accessToken
$headers = @{ Authorization = "Bearer $token" }
```

관리자 토큰으로 주문을 등록합니다.

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/orders" -Headers $headers -ContentType "application/json" -Body '{"recipientName":"김민지","recipientPhone":"010-1234-5678","address":"서울특별시 강남구 테헤란로 101","requestedDate":"2026-08-14"}'
```

## 현재 권한 규칙

| API | 권한 |
|---|---|
| `POST /api/v1/auth/login` | 공개 |
| 주문 등록·조회 | 관리자 |
| 배송 기사 등록·조회 | 관리자 |
| 배송 배정 | 관리자 |
| 배송 상태 변경·이력 조회 | 관리자 또는 배정된 배송 기사 |

배송 기사는 본인에게 배정된 배송만 상태 변경 또는 이력 조회를 할 수 있습니다. 변경 이력의 처리자 이름은 요청 본문이 아니라 JWT의 로그인 이메일로 자동 기록됩니다.

## 응답 코드

- `401 Unauthorized`: 토큰이 없거나 유효하지 않습니다.
- `403 Forbidden`: 로그인은 했지만 해당 API의 역할 또는 배송 소유 권한이 없습니다.
