# Real Korea Travel

외국인 관광객을 위한 한국 로컬 맛집/카페 추천 플랫폼 (MVP)

> **Travel Korea Like a Local.** 한국인처럼 여행하는 가장 쉬운 방법

외국인이 **"한국인이라면 어디를 선택할까?"** 라는 질문에 답을 주는 의사결정 플랫폼.

## MVP 범위
- **지역**: 서울 (성수, 홍대, 강남, 명동)
- **카테고리**: 맛집, 카페, 디저트, 술집
- **기능**: 장소 탐색·상세, 검색/필터, Local Score, AI 리뷰 요약, 즐겨찾기, Google Maps 길찾기

## 기술 스택 (계획)
- Backend: Java 25, Spring Boot 4.x, Spring Data JPA, Spring Security
- DB: PostgreSQL 18, Cache: Redis 8
- AI: GPT (리뷰 요약), Search: PostgreSQL FTS
- Map: Google Maps SDK / Places API
- Storage: S3, Infra: Docker + AWS (ECS, RDS, CloudFront)

## 문서
- PRD / ERD / OpenAPI: Notion (Real Korea Travel Project)
- 작업 관리: Jira `RKT` 프로젝트
- 로컬 규칙: `AGENTS.md`, `memory-bank/`
