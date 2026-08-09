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
2. 프로젝트 생성 & 모듈 구조 (RKT-9)
3. Docker 로컬 개발 환경 (RKT-10) — PostgreSQL 18, Redis 8 컨테이너
4. DB 스키마 초기화 (RKT-11) — Flyway, `flyway-database-postgresql` 모듈 필수

## 로컬 저장소
- 프로젝트 루트: `Real_Korea_Travel/`
- 브랜치: `main` (배포 기준), 작업 브랜치 `feature/{JiraKey}-{summary}`
- 문서: Notion(PRD/ERD/OpenAPI)이 원본, `memory-bank/`는 로컬 요약

상세 작업 단계는 `memory-bank/project-brief.md`와 `AGENTS.md`를 따른다.
> Git 브랜치/커밋은 GitHub 연동(Jira 자동화)과 함께 연동 확인됨.
