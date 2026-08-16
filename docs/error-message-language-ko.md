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

## 메시지 키 기반 관리

서비스와 도메인 코드는 사용자에게 보여 줄 한글·영문 문구를 직접 작성하지 않습니다. 대신 `ApiException`에 `error.delivery.notFound`와 같은 메시지 키를 전달하고, 전역 예외 처리기가 현재 요청의 `Accept-Language`에 맞는 문구를 선택합니다.

```java
throw ApiException.notFound("error.delivery.notFound");
```

언어별 문구는 아래 파일에서 관리합니다.

- 기본 한국어: `src/main/resources/messages.properties`
- 영어: `src/main/resources/messages_en.properties`

상태 전이처럼 값이 필요한 메시지는 `{0}`, `{1}` 자리표시자를 사용합니다. 따라서 API의 `code`는 고정하고, `message`만 요청 언어에 맞춰 표시할 수 있습니다.