# 처음부터 로컬 테스트하기

이 문서는 다른 개발자나 면접관이 DeliveryFlow 저장소를 받은 뒤, 본인 PC에서 안전하게 전체 기능을 실행하고 테스트하는 방법입니다.

공개 Railway 환경의 테스트 계정 비밀번호는 제공하지 않습니다. 대신 각자 독립된 로컬 Docker 환경에서 자신의 비밀번호로 테스트하는 방식을 권장합니다.

## 1. 준비물

- Git
- Docker Desktop
- Docker Desktop이 실행 중인 상태

Java나 PostgreSQL을 PC에 별도로 설치하지 않아도 됩니다. Docker Compose가 애플리케이션과 PostgreSQL을 함께 실행합니다.

## 2. 저장소 내려받기

```powershell
git clone https://github.com/cybdream/deliveryflow.git
cd deliveryflow
```

## 3. 개인 설정 파일 만들기

예제 파일을 복사합니다.

```powershell
Copy-Item .env.example .env
```

`.env`를 열어 아래 값을 본인의 값으로 바꿉니다. `.env`는 Git에서 제외되므로 GitHub에 올리지 않습니다.

```text
POSTGRES_PASSWORD=본인의_로컬_DB_비밀번호
APP_JWT_SECRET=32바이트_이상의_Base64_무작위_문자열
APP_BOOTSTRAP_ADMIN_EMAIL=admin@deliveryflow.local
APP_BOOTSTRAP_ADMIN_PASSWORD=본인의_관리자_비밀번호
APP_BOOTSTRAP_DRIVER_EMAIL=driver@deliveryflow.local
APP_BOOTSTRAP_DRIVER_PASSWORD=본인의_기사_비밀번호
```

`APP_JWT_SECRET`은 예를 들어 PowerShell에서 다음 명령으로 만들 수 있습니다.

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

## 4. 실행

```powershell
docker compose up --build
```

로그에 애플리케이션 시작 메시지가 나오면 다음 주소를 엽니다.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- 상태 확인: `http://localhost:8080/api/v1/health`

종료하려면 실행 창에서 `Ctrl + C`를 누릅니다.

## 5. 전체 흐름 자동 테스트

다른 PowerShell 창에서 프로젝트 폴더로 이동한 뒤 실행합니다.

```powershell
.\scripts\test-delivery-flow.ps1
```

입력할 값은 `.env`에 설정한 관리자와 기사 비밀번호입니다. 스크립트는 다음을 자동 확인합니다.

1. 상태 확인
2. 관리자와 기사 로그인
3. 주문 등록
4. 기사 배정
5. 기사 본인 배송 목록 조회
6. 배송 시작과 배송 완료
7. 고객용 주문번호·운송장번호 배송 조회 API 확인
8. 배송 이력과 대시보드 조회

실제 주문과 배송 데이터가 생성되므로, 마지막 확인에서 `RUN`을 입력해야만 테스트가 진행됩니다.

## 6. Swagger로 직접 테스트하기

1. `POST /api/v1/auth/login`에서 관리자 또는 기사 계정으로 로그인합니다.
2. 응답의 `accessToken`을 Swagger 오른쪽 위 **Authorize**에 입력합니다.
3. 관리자: 주문 등록, 기사 조회, 배송 배정을 수행합니다.
4. 기사: `GET /api/v1/deliveries/me` 조회 후 배송 상태를 `IN_DELIVERY`, `DELIVERED`로 변경합니다.
5. 관리자: 배송 이력과 대시보드를 조회합니다.

## 7. 비밀번호를 바꾸거나 처음부터 다시 시작하려면

계정은 DB에 최초 생성될 때의 비밀번호를 사용합니다. `.env`의 비밀번호만 나중에 바꿔도 기존 계정 비밀번호는 자동으로 바뀌지 않습니다.

연습 데이터를 모두 지우고 처음부터 시작해도 된다면 다음을 실행합니다.

```powershell
docker compose down -v
docker compose up --build
```

`down -v`는 주문·배송·계정 데이터를 포함한 로컬 PostgreSQL 볼륨을 삭제합니다. 필요한 데이터가 있다면 사용하지 마세요.

호스트 PostgreSQL을 직접 설치해 실행한 경우에는 [로컬 계정 비밀번호 재설정](local-account-password-reset-ko.md) 문서를 사용합니다.

## 8. Railway 공개 배포 환경

- API: `https://deliveryflow-production.up.railway.app`
- Swagger UI: `https://deliveryflow-production.up.railway.app/swagger-ui/index.html`

공개 주소에서는 상태 확인과 Swagger 화면을 볼 수 있습니다. 보호된 API를 시험하려면 별도로 제공받은 테스트 계정이 필요하며, 실제 배포 데이터가 추가될 수 있으므로 일반적인 기능 확인은 로컬 Docker 환경을 권장합니다.
