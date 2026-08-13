# 오류 메시지 언어 선택

기본 오류 메시지 언어는 한국어입니다. 요청 헤더에 `Accept-Language: en`을 넣으면 지원되는 표준 오류 메시지가 영어로 반환됩니다.

```powershell
$englishDriverHeaders = @{
  Authorization = "Bearer $($driverLogin.accessToken)"
  "Accept-Language" = "en"
}

Invoke-RestMethod "http://localhost:8080/api/v1/orders" -Headers $englishDriverHeaders
```

배송 기사 토큰으로 관리자 전용 주문 목록을 호출하면 아래와 유사한 영문 오류를 받습니다.

```json
{
  "status": 403,
  "code": "FORBIDDEN",
  "message": "You do not have permission to perform this action.",
  "path": "/api/v1/orders"
}
```

`code` 값은 언어와 무관하게 고정됩니다. 화면이나 다른 시스템은 `code`로 처리하고, 사용자에게 보여줄 문구에는 `message`를 사용합니다.
