# 표준 오류 응답

모든 API 오류는 아래 형식으로 반환합니다.

```json
{
  "timestamp": "2026-08-13T11:15:00",
  "status": 400,
  "code": "BUSINESS_RULE_VIOLATION",
  "message": "이미 배송이 배정된 주문입니다.",
  "path": "/api/v1/deliveries",
  "fieldErrors": {}
}
```

| 상태 | 코드 예시 | 의미 |
|---|---|---|
| 400 | `VALIDATION_FAILED` | 요청 필드가 누락되었거나 형식이 올바르지 않음 |
| 400 | `MALFORMED_REQUEST` | JSON 요청 본문 형식이 올바르지 않음 |
| 400 | `BUSINESS_RULE_VIOLATION` | 배송 중복 배정 등 업무 규칙 위반 |
| 401 | `UNAUTHORIZED` | 토큰이 없거나 만료·변조됨 |
| 403 | `FORBIDDEN` | 로그인했지만 역할 또는 배송 소유 권한이 없음 |

입력 검증 오류는 `fieldErrors`에 필드별 메시지가 추가됩니다.
