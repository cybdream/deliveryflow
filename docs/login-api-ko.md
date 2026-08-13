# 로그인 API

## 요청

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@deliveryflow.local",
  "password": "로컬 설정 파일에 입력한 관리자 비밀번호"
}
```

## 성공 응답

```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "role": "ADMIN",
  "name": "Local Administrator"
}
```

토큰은 다음 인증·권한 단계에서 요청 헤더에 사용합니다.

```http
Authorization: Bearer {accessToken}
```

## 현재 범위

- 이메일과 BCrypt 비밀번호 해시를 비교합니다.
- 활성 사용자만 로그인할 수 있습니다.
- 토큰에는 이메일과 역할 정보를 담습니다.
- 현재 단계에서는 토큰을 발급하지만, API 요청을 차단하는 보안 필터는 다음 단계에서 추가합니다.
