# 개발 시작 가이드 (RKT-8)

Real Korea Travel 백엔드 개발 시작을 위한 가이드. 기술 버전은 `memory-bank/tech-stack.md`의 확정 스택을 따른다.

## 확정 기술 스택 (RKT-8, 2026-08-09)

|영역|기술|버전|
|---|---|---|
|언어|Java|25 (LTS)|
|프레임워크|Spring Boot|4.1.0|
|ORM|Spring Data JPA (Hibernate 7.4)|4.1.0 (관리)|
|DB|PostgreSQL|18.4|
|Cache|Redis|8.10.0|
|보안|Spring Security 7.1.0 (관리)|-|

## 초기 작업 순서 (E1 인프라)
1. 기술 스택 버전 확정 (RKT-8) ✅
2. 프로젝트 생성 & 모듈 구조 (RKT-9) ✅
3. Docker 로컬 개발 환경 (RKT-10) — PostgreSQL 18, Redis 8 컨테이너 ✅

## Redis 캐시 (RKT-21)

- `placeList`: 장소 목록 응답을 5분 동안 캐싱한다.
- `placeDetail`: 장소 상세 응답을 10분 동안 캐싱한다.
- `searchResult`: 검색 조건별 결과를 3분 동안 캐싱한다.
- 캐시 키에는 요청 조건 또는 장소 ID가 포함되며, 동일 요청은 Redis 값을 우선 사용한다.
- Local Score 전용 캐시는 Local Score 기능 구현 후 별도로 연계한다.
4. DB 스키마 초기화 (RKT-11) — Flyway, `flyway-database-postgresql` 모듈 필수

## 로컬 개발 환경 (RKT-10)
Docker Desktop 실행 후 프로젝트 루트에서:

```bash
docker compose up -d     # PostgreSQL 18.4 + Redis 8.10 기동
docker compose ps        # 상태 확인 (둘 다 healthy)
docker compose down      # 정지 (데이터 유지)
docker compose down -v   # 볼륨까지 삭제
```

- **PostgreSQL 18.4** — `localhost:5432`, DB/User/Password `realkorea`/`realkorea`/`realkorea1234`
- **Redis 8.10** — `localhost:6379`
- 데이터는 named volume(`postgres-data`, `redis-data`)에 유지되어 `down` 후에도 보존
- PostgreSQL 18+ 이미지는 데이터 파일 상위 디렉터리(`/var/lib/postgresql`)에 볼륨을 마운트해야 한다 (전용 하위 디렉터리 사용)

## 테스트 환경 (RKT-33)

- Controller·Service 단위 테스트는 외부 DB 없이 실행한다.
- Repository·Integration 테스트는 운영과 동일한 PostgreSQL 18.4 Testcontainers를 사용한다.
- Testcontainers 테스트를 실행하려면 Docker Desktop이 실행 중이어야 한다.
- 테스트 실행 명령: `./gradlew test`
- 새 Repository·Integration 테스트에는 H2를 사용하지 않는다. 기존 H2 테스트는 범위와 위험도를 검토해 단계적으로 전환한다.

## DB 스키마 (RKT-11, Flyway)

- 마이그레이션 파일: `backend/src/main/resources/db/migration/` (`V1__create_tables.sql`, `V2__seed_base_data.sql`)
- 앱 기동(`./gradlew bootRun`) 시 Flyway가 자동 적용된다. `ddl-auto: validate`이므로 스키마는 Flyway가 유일하게 관리한다.
- 스키마 변경 시 기존 파일은 수정하지 않고 새 버전 `V{n}__*.sql`로 추가한다.
- 검증: `psql -U realkorea -d realkorea -c "SELECT * FROM flyway_schema_history;"`

## Google OAuth 로그인 (RKT-14)

- **의존성**: `spring-boot-starter-oauth2-client` 추가됨
- **설정**: `application.yml`에서 `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` 환경변수로 주입
- **엔드포인트**:
  - `GET /auth/oauth2/google` — Google 인가 URL로 302 리다이렉트 (진입점)
  - `GET /auth/oauth2/google/callback` — 승인 코드로 Google 토큰/사용자 정보 교환 → Member 조회/생성 → JWT 발급
- **로컬 테스트**:
  1. [Google Cloud Console](https://console.cloud.google.com)에서 OAuth Client ID 생성
  2. 승인된 리다이렉트 URI에 `http://localhost:8080/auth/oauth2/google/callback` 등록
  3. `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` 환경변수 설정 후 `./gradlew bootRun`
  4. 브라우저에서 `http://localhost:8080/auth/oauth2/google` 접속 → 구글 로그인 → 콜백에서 JWT 응답 확인

## JWT 인증 (RKT-15)

- **설정**: `JWT_SECRET` 환경변수는 32자 이상으로 설정한다.
- **만료 시간**:
  - access token: 기본 3600초(1시간) (`JWT_ACCESS_EXPIRATION_SECONDS`)
  - refresh token: 기본 1,209,600초 (`JWT_REFRESH_EXPIRATION_SECONDS`)
- **로그인 성공 응답**: Google OAuth callback에서 access token과 refresh token을 발급한다.
- **토큰 갱신**: `POST /api/v1/auth/refresh`에 `{"refreshToken":"..."}`를 전달한다.
- **갱신 실패**: refresh token이 누락되거나 유효하지 않으면 `400` 또는 `401`을 반환한다.
- **인증 요청**: 보호된 API 호출 시 `Authorization: Bearer {accessToken}` 헤더를 사용한다.
- **주의**: JWT secret은 소스 코드나 로그에 기록하지 않는다.

## 로컬 저장소
- 프로젝트 루트: `Real_Korea_Travel/`
- 브랜치: `main` (배포 기준), 작업 브랜치 `feature/{JiraKey}-{summary}`
- 문서 역할: Notion(PRD/ERD/OpenAPI)이 공식 기준, `docs/ERD.md`는 AI Agent·코드 리뷰용 ERD 요약, `memory-bank/`는 로컬 Context/Cache
- ERD 상세 설계는 [Notion ERD](https://app.notion.com/p/ERD-3b38a222ce3c804da4e6d8165ad1fbfb)를 확인하고, 실제 적용 DB는 `backend/src/main/resources/db/migration/`을 확인한다.

## Spring Security API 보호 (RKT-17)

- **공개 엔드포인트**: `/auth/oauth2/**`, `/api/v1/auth/**`, `/actuator/health`, `/actuator/info`
- **보호 엔드포인트**: 공개 목록에 포함되지 않은 모든 요청은 유효한 access token이 필요하다.
- **인증 헤더**: 보호된 API 호출 시 `Authorization: Bearer {accessToken}` 헤더를 사용한다.
- **세션 정책**: JWT 기반 Stateless 인증을 사용하므로 서버 세션에 인증 상태를 저장하지 않는다.
- **인증 실패**: 토큰이 없거나 유효하지 않으면 `401 Unauthorized`를 반환한다.
- **인가 실패**: 인증은 성공했지만 필요한 권한이 없으면 `403 Forbidden`을 반환한다.

## 장소 목록 API (RKT-18)

- **엔드포인트**: `GET /api/v1/places`
- **필터**: `region`, `category` 코드는 소문자로 전달한다. 예: `?region=seongsu&category=cafe`
- **페이징**: `page`는 0부터 시작하고 `size` 기본값은 20, 최대값은 100이다.
- **정렬**: `sort`는 `name`, `createdAt`, `updatedAt` 필드와 `asc`·`desc` 방향을 지원한다.
- **조회 대상**: `ACTIVE` 상태이고 soft delete되지 않은 장소만 반환한다.

## 장소 상세 조회 API (RKT-19)

- **엔드포인트**: `GET /api/v1/places/{placeId}`
- **조회 대상**: `ACTIVE` 상태이고 soft delete되지 않은 장소만 반환한다.
- **응답 범위**: 장소 기본 정보, 지역·카테고리, 외국인 편의정보(`PlaceFeature`), 메뉴 목록, Local Score를 포함한다.
- **메뉴 정렬**: `sort_order` 오름차순을 기본으로 하며, 같은 순서에서는 메뉴 ID 오름차순으로 정렬한다.
- **예외**: 장소가 존재하지 않거나 비활성·삭제 상태이면 `404 Not Found`를 반환한다.
- **Local Score**: 종합 점수와 음식·가격·분위기·재방문·현지인 추천 세부 점수를 포함하며, 점수가 없으면 `null`을 반환한다.

## 키워드 검색 API (RKT-20)

- **엔드포인트**: `GET /api/v1/search?keyword={keyword}`
- **검색 방식**: 장소명·주소·설명에 PostgreSQL `ILIKE` 부분 문자열 검색을 적용한다.
- **필터**: `region`, `category`, `englishMenu`, `soloFriendly`, `cardAvailable`, `localRecommended`, `maxWaitTimeMin`을 선택적으로 조합한다.
- **페이징**: `page`는 0부터 시작하고 `size` 기본값은 20, 최대값은 100이다.
- **추천 기준**: 초기 구현에서는 `local_score.local_recommend_score >= 70`인 장소를 `localRecommended=true`로 판단한다.
- **조회 대상**: `ACTIVE` 상태이고 soft delete되지 않은 장소만 반환한다.
- **검색어 처리**: 검색어 앞뒤 공백을 제거한 뒤 장소명·주소·설명 중 하나라도 포함하면 검색 결과에 포함한다.

## 현재 API 구현 범위 정리

- 현재 구현된 인증 진입점은 `GET /auth/oauth2/google`, `GET /auth/oauth2/google/callback`, 토큰 갱신은 `POST /api/v1/auth/refresh`이다.
- 장소 목록 응답은 `places`와 `page`, `size`, `totalElements`, `totalPages`를 사용한다. 기본 정렬은 `createdAt,desc`이다.
- 장소 상세 응답은 장소 기본 정보, 지역·카테고리, `PlaceFeature`, `recommendedMenus`, `localScore`를 제공한다. 이미지·운영시간·AI 리뷰 요약·Local Tip은 후속 도메인 구현 범위다.
- 키워드 검색은 `GET /api/v1/search`로 구현되었고, 즐겨찾기 등록은 인증된 회원이 `POST /api/v1/bookmarks`로 요청할 수 있다. Local Score 고도화와 AI 리뷰 요약은 후속 구현 범위다.

상세 작업 단계는 `memory-bank/project-brief.md`와 `AGENTS.md`를 따른다.
> Git 브랜치/커밋은 GitHub 연동(Jira 자동화)과 함께 연동 확인됨.
