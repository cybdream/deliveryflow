# 로컬 계정 비밀번호 재설정

로컬 PostgreSQL에 저장된 DeliveryFlow 관리자 또는 기사 계정의 비밀번호를 재설정하는 도구입니다.

- 스크립트: `scripts/reset-local-account-password.ps1`
- 대상은 `localhost` 데이터베이스로 제한됩니다. Railway 등 원격 DB에는 사용할 수 없습니다.
- PostgreSQL 비밀번호와 새 계정 비밀번호는 입력 중에 표시되거나 파일에 저장되지 않습니다.
- 실행 직전에 `RESET`을 입력해야 실제 DB 변경이 발생합니다.

## 언제 사용하나요?

`application-local.properties`의 bootstrap 비밀번호를 바꿨지만, 기존 `admin@deliveryflow.local` 또는 `driver@deliveryflow.local` 계정으로 로그인할 수 없을 때 사용합니다.

bootstrap 설정은 계정이 없는 첫 시작에만 적용됩니다. 이미 DB에 계정이 있으면 자동으로 비밀번호를 바꾸지 않습니다.

## 실행 방법

프로젝트 루트에서 실행합니다.

```powershell
.\scripts\reset-local-account-password.ps1
```

기본값은 관리자와 기사 계정을 모두 재설정합니다. 관리자만 재설정하려면 다음을 사용합니다.

```powershell
.\scripts\reset-local-account-password.ps1 -Account admin
```

기사만 재설정할 때는 `-Account driver`를 사용합니다.

## 입력 순서

1. 로컬 PostgreSQL `postgres` 계정 비밀번호
2. 새 관리자 비밀번호와 확인 입력
3. 새 기사 비밀번호와 확인 입력
4. `RESET` 확인 문자열

새 비밀번호는 8자 이상이어야 합니다. 재설정 후 애플리케이션을 다시 시작할 필요는 없으며, 새 비밀번호로 로그인하거나 `test-delivery-flow.ps1`을 다시 실행하면 됩니다.

## 동작 방식

스크립트는 PostgreSQL의 `pgcrypto` 확장을 사용해 BCrypt 형식의 비밀번호 해시를 만들고, 활성 상태의 `ADMIN` 또는 `DRIVER` 계정만 업데이트합니다. 대상 계정이 없으면 변경하지 않고 오류를 표시합니다. 입력값은 PostgreSQL 문자열 리터럴로 이스케이프해 SQL에 전달합니다.
