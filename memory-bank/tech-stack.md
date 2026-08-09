# Tech Stack — Real Korea Travel

> 확정일: 2026-08-09 (RKT-8 기술 스택 버전 확정)

## 확정 스택

|영역|기술|버전(확정)|비고|
|---|---|---|---|
|언어|Java|25 (LTS)|GA 2025-09-16, Spring Boot 4.1 지원 범위(17~26) 내|
|프레임워크|Spring Boot|4.1.0|GA 2026-06-10, Spring Framework 7.0.8 기반|
|ORM|Spring Data JPA|4.1.0 (관리)|Hibernate ORM 7.4 + Postgres JDBC 42.7.11|
|보안|Spring Security|7.1.0 (관리)|Spring Boot 4.1.0 기본 관리 버전|
|DB|PostgreSQL|18.4|2026-05-14 패치, EOL 2030-11|
|Cache|Redis|8.10.0|GA 2026-07-29, Docker 이미지 `redis:8.10`|
|AI|GPT API|5.5 모델|P1 스케줄|
|Search|PostgreSQL Full Text Search|-|MVP 사용|
|Map|Google Maps SDK / Places API|외부 연동|구현 시|
|Storage|Amazon S3|외부 연동|구현 시|
|Infra|Docker + AWS(ECS, RDS, CloudFront)|외부|E1/E13 (Docker는 RKT-10)|

## 버전 확정 근거 (2026-08-09 기준)

- **Spring Boot**: 최신 안정 4.1.0 (2026-06-10). 3.5.x는 OSS 지원 종료(2026-06-30).
- **Java**: 25는 최신 LTS. Spring Boot 4.1 지원 범위(Java 17~26)에 포함되어 호환.
- **Spring Boot ↔ Java**: 4.x는 Java 17 최소 요구, 최대 26 지원 → Java 25 사용 안전.
- **JPA ↔ PostgreSQL**: Spring Data JPA 4.1.0 관리 Hibernate 7.4 (PostgreSQLDialect 최소 버전 14). Postgres JDBC 42.7.11 관리. PostgreSQL 18.4 정상 지원.
- **Redis**: 8.10.0(현재 최신 GA). Spring Data Redis(Lettuce)가 Redis 8.x RESP와 호환.

## 주의사항
- **Flyway + PostgreSQL 18**: `spring-boot-starter-flyway`만으로는 "Unsupported Database: PostgreSQL 18" 발생. 반드시 `org.flywaydb:flyway-database-postgresql` 의존성을 추가해야 한다 (RKT-11 반영).
- Redis Docker 이미지는 `redis:8.10` 사용 가능. 특정 마이너 고정 필요하면 `redeis:8.10.0`.

## 관련 티켓
- RKT-8 (본건), RKT-9 (프로젝트 생성에서 반영), RKT-10 (Docker), RKT-11 (Flyway)