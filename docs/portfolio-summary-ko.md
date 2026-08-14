# DeliveryFlow 포트폴리오 소개

## 한 줄 소개

배송 접수부터 기사 배정, 배송 상태 전이와 변경 이력까지 관리하는 Spring Boot 기반 배송 운영 관리 API입니다.

## 문제와 해결

배송 업무에서는 잘못된 상태 변경, 역할에 맞지 않는 접근, 변경 이력 누락이 운영 오류로 이어질 수 있습니다. DeliveryFlow는 JWT 기반 역할 권한, 상태 전이 검증, 배송 이력 보관을 통해 이 문제를 API 수준에서 제어합니다.

## 구현 범위

- 관리자와 기사의 JWT 로그인 및 역할별 접근 제어
- 주문 등록·조회, 기사 관리, 배송 배정
- 배송 상태 변경 및 변경 이력 조회
- 운영 대시보드와 표준 오류 응답
- Swagger API 문서, Docker Compose, GitHub Actions CI, Railway 배포

## 검증 결과

Railway 운영 환경에서 관리자 로그인, 주문 등록, 주문 조회를 외부 접속으로 검증했습니다. 추가 배송 배정·상태 변경 검증은 `railway-external-verification-ko.md` 체크리스트에 따라 진행할 수 있습니다.

## 링크

- GitHub: https://github.com/cybdream/deliveryflow
- Live API: https://deliveryflow-production.up.railway.app
- Swagger UI: https://deliveryflow-production.up.railway.app/swagger-ui/index.html
