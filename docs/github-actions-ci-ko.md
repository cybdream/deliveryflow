# GitHub Actions 자동 테스트

`main` 브랜치에 push하거나 Pull Request를 만들면 GitHub Actions가 자동으로 실행됩니다.

- Java 17 환경을 준비합니다.
- Gradle 의존성을 캐시합니다.
- `./gradlew test --no-daemon`으로 전체 자동 테스트를 실행합니다.

통합 테스트는 실제 PostgreSQL이나 Docker를 사용하지 않습니다. H2 인메모리 DB를 사용하므로 GitHub에서도 별도 비밀번호 설정 없이 실행됩니다.

현재 확인하는 주요 흐름은 다음과 같습니다.

- 관리자 로그인 시 JWT 발급
- 토큰 없이 주문 목록을 요청하면 401 응답
- 관리자 토큰으로 `createdAt,desc` 정렬 주문 목록 조회

GitHub 저장소의 **Actions** 탭에서 `CI` 실행 결과가 초록색 체크 표시인지 확인하면 됩니다.
