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

`.env` 파일에서 아래 값을 실제 값으로 바꿉니다. 이 파일은 Git에 올리지 않습니다.

```text
POSTGRES_PASSWORD=PostgreSQL 비밀번호
APP_JWT_SECRET=32바이트 이상 무작위 값의 Base64 문자열
APP_BOOTSTRAP_ADMIN_EMAIL=admin@deliveryflow.local
APP_BOOTSTRAP_ADMIN_PASSWORD=관리자 로그인 비밀번호
APP_BOOTSTRAP_DRIVER_EMAIL=driver@deliveryflow.local
APP_BOOTSTRAP_DRIVER_PASSWORD=기사 로그인 비밀번호
```

관리자와 기사 계정은 해당 이메일이 데이터베이스에 없을 때 한 번만 만들어집니다. 이미 만들어진 계정의 비밀번호를 바꾸려면, 초기화 값 대신 별도의 비밀번호 변경 기능을 구현하는 방식이 적절합니다.

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

