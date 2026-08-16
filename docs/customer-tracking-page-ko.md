# 고객 배송 조회 화면

DeliveryFlow의 공개 API를 실제 고객이 사용할 수 있는 간단한 웹 화면으로 제공했습니다.

- 주소: `/`
- 로컬: `http://localhost:8080/`
- Railway: `https://deliveryflow-production.up.railway.app/`

## 사용 방법

1. **주문번호로 조회** 또는 **배송 추적번호로 조회**를 선택합니다.
2. 해당 번호와 수령인 전화번호를 입력합니다.
3. **배송 조회하기**를 누릅니다.

조회 성공 시 주문번호, 배송 추적번호, 현재 상태, 예정일, 완료일만 표시합니다. 주소나 기사 정보 등 불필요한 개인정보는 표시하지 않습니다.

## 구현 방식

Spring Boot의 정적 파일 기능으로 `src/main/resources/static/index.html`을 제공했습니다. 별도의 프론트엔드 서버나 로그인 없이, 같은 도메인의 공개 배송 조회 API를 호출합니다.

- 주문번호 조회: `GET /api/v1/tracking/orders/{orderNo}`
- 배송 추적번호 조회: `GET /api/v1/tracking/shipments/{trackingNo}`
- 두 API 모두 수령인 전화번호 검증이 필요합니다.

API가 `404 Not Found`를 반환하면 화면에서는 주문번호·추적번호 또는 전화번호를 다시 확인하도록 안내합니다.