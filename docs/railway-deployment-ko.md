# Railway 배포 안내

## 1. 배포 전에 반영된 구성

- Railway가 지정하는 `PORT`를 받아 앱을 실행합니다.
- `prod` 프로필에서 Railway PostgreSQL 연결 변수(`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`)를 사용합니다.
- `GET /api/v1/health`는 로그인 없이 `{"status":"UP"}`을 반환합니다.
- Swagger는 포트폴리오 공개를 위해 기본 활성화됩니다. 운영 공개를 원하지 않으면 `APP_SWAGGER_ENABLED=false`로 설정합니다.

## 2. Railway 프로젝트 구성

1. GitHub 저장소 `cybdream/deliveryflow`로 새 서비스를 만듭니다. Dockerfile이 자동으로 감지됩니다.
2. 같은 프로젝트에서 **New → Database → PostgreSQL**을 추가합니다.
3. 앱 서비스의 **Variables**에서 아래 값을 설정합니다.

```text
SPRING_PROFILES_ACTIVE=prod
PGHOST=${{Postgres.PGHOST}}
PGPORT=${{Postgres.PGPORT}}
PGDATABASE=${{Postgres.PGDATABASE}}
PGUSER=${{Postgres.PGUSER}}
PGPASSWORD=${{Postgres.PGPASSWORD}}
APP_JWT_SECRET=새로_생성한_Base64_비밀값
APP_BOOTSTRAP_ADMIN_EMAIL=admin@deliveryflow.local
APP_BOOTSTRAP_ADMIN_PASSWORD=새_관리자_비밀번호
APP_BOOTSTRAP_DRIVER_EMAIL=driver@deliveryflow.local
APP_BOOTSTRAP_DRIVER_PASSWORD=새_기사_비밀번호
APP_SWAGGER_ENABLED=true
```

`Postgres`는 Railway PostgreSQL 서비스 이름입니다. 화면에서 실제 서비스 이름이 다르면 그 이름으로 바꾸거나 변수 입력 시 Railway의 자동완성 항목을 선택합니다.

`APP_JWT_SECRET`은 PowerShell에서 다음 명령으로 생성할 수 있습니다.

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
[Convert]::ToBase64String($bytes)
```

실제 비밀번호·JWT 비밀값은 GitHub, 소스 코드, README에 저장하지 않습니다.

## 데모 계정 공개 원칙

README에는 관리자와 기사 계정의 이메일·역할만 안내하고 실제 비밀번호는 게시하지 않습니다. 관리자 계정은 주문·기사·배송 데이터를 변경할 수 있으므로, 외부 테스트가 필요한 경우에만 별도로 접근 정보를 제공합니다.
## 3. 배포 확인

배포 로그에 `Started DeliveryflowApplication`이 보이면, 서비스 Settings의 **Networking → Generate Domain**으로 공개 주소를 만듭니다.

- 상태 확인: `https://발급된-도메인/api/v1/health`
- Swagger: `https://발급된-도메인/swagger-ui/index.html`

Swagger UI에서 `Failed to fetch`가 보이면, Railway 프록시 주소 처리가 포함된 최신 배포인지 확인하고 새 배포가 완료된 뒤 브라우저를 새로고침합니다.

첫 배포 때 데이터베이스에 관리자·기사 이메일이 없으면 `APP_BOOTSTRAP_*` 값으로 계정이 한 번 생성됩니다.

