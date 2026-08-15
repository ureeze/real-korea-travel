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
  - access token: 기본 900초 (`JWT_ACCESS_EXPIRATION_SECONDS`)
  - refresh token: 기본 1,209,600초 (`JWT_REFRESH_EXPIRATION_SECONDS`)
- **로그인 성공 응답**: Google OAuth callback에서 access token과 refresh token을 발급한다.
- **토큰 갱신**: `POST /api/v1/auth/refresh`에 `{"refreshToken":"..."}`를 전달한다.
- **갱신 실패**: refresh token이 누락되거나 유효하지 않으면 `400` 또는 `401`을 반환한다.
- **인증 요청**: 보호된 API 호출 시 `Authorization: Bearer {accessToken}` 헤더를 사용한다.
- **주의**: JWT secret은 소스 코드나 로그에 기록하지 않는다.

## 로컬 저장소
- 프로젝트 루트: `Real_Korea_Travel/`
- 브랜치: `main` (배포 기준), 작업 브랜치 `feature/{JiraKey}-{summary}`
- 문서: Notion(PRD/ERD/OpenAPI)이 원본, `memory-bank/`는 로컬 요약

## Spring Security API 보호 (RKT-17)

- **공개 엔드포인트**: `/auth/oauth2/**`, `/api/v1/auth/**`, `/actuator/health`, `/actuator/info`
- **보호 엔드포인트**: 공개 목록에 포함되지 않은 모든 요청은 유효한 access token이 필요하다.
- **인증 헤더**: 보호된 API 호출 시 `Authorization: Bearer {accessToken}` 헤더를 사용한다.
- **세션 정책**: JWT 기반 Stateless 인증을 사용하므로 서버 세션에 인증 상태를 저장하지 않는다.
- **인증 실패**: 토큰이 없거나 유효하지 않으면 `401 Unauthorized`를 반환한다.
- **인가 실패**: 인증은 성공했지만 필요한 권한이 없으면 `403 Forbidden`을 반환한다.

상세 작업 단계는 `memory-bank/project-brief.md`와 `AGENTS.md`를 따른다.
> Git 브랜치/커밋은 GitHub 연동(Jira 자동화)과 함께 연동 확인됨.
