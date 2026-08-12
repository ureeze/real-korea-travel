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

## 로컬 저장소
- 프로젝트 루트: `Real_Korea_Travel/`
- 브랜치: `main` (배포 기준), 작업 브랜치 `feature/{JiraKey}-{summary}`
- 문서: Notion(PRD/ERD/OpenAPI)이 원본, `memory-bank/`는 로컬 요약

상세 작업 단계는 `memory-bank/project-brief.md`와 `AGENTS.md`를 따른다.
> Git 브랜치/커밋은 GitHub 연동(Jira 자동화)과 함께 연동 확인됨.
