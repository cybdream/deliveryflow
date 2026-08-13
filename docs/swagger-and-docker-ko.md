# Swagger API 문서와 Docker 실행

## Swagger UI

앱을 재시작한 뒤 브라우저에서 아래 주소를 엽니다.

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON 문서는 아래 주소입니다.

```text
http://localhost:8080/v3/api-docs
```

Swagger UI 오른쪽 위 **Authorize** 버튼을 누르고, 로그인으로 받은 JWT를 입력하면 보호된 API를 직접 테스트할 수 있습니다.

```text
Bearer {accessToken}
```

## Docker Compose

Docker Desktop 설치 후, 프로젝트 폴더에서 다음을 실행합니다.

```powershell
Copy-Item .env.example .env
```

`.env` 파일의 `POSTGRES_PASSWORD`와 `APP_JWT_SECRET`을 실제 값으로 바꾼 뒤 실행합니다.

```powershell
docker compose up --build
```

- 앱: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- PostgreSQL: `localhost:5432`

중지하려면:

```powershell
docker compose down
```

데이터베이스 데이터까지 지우려면 `docker compose down -v`를 사용합니다.
