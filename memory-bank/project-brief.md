# Project Brief — Real Korea Travel

> Memory Bank의 구조와 각 문서의 역할을 정의하는 기준 문서.

## Memory Bank 구조

| 문서 | 역할 |
|---|---|
| `project-brief.md` | 프로젝트 개요, MVP 범위, 성공 기준, Memory Bank 구조(본 문서) |
| `architecture.md` | 시스템 구성, 모듈/패키지 구조, 아키텍처 결정 |
| `tech-stack.md` | 기술 스택과 버전, 사용 이유 (E1에서 확정) |
| `coding-rules.md` | 코딩 표기 규칙, 용어, 컨벤션 |
| `decisions.md` | ADR 집약 (결정 사항 인덱스) |

- Notion PRD·ERD·OpenAPI가 공식 문서다. Memory Bank는 AI Agent가 작업을 빠르게 이해하기 위한 로컬 Context/Cache다.
- DB 설계는 Notion ERD, 실제 적용 스키마는 Flyway Migration, API 계약은 Notion OpenAPI를 기준으로 한다.
- 로컬 요약과 공식 문서가 충돌하면 코드·테스트·Migration·최근 결정 내역을 함께 확인하고, 작업 종료 시 요약을 갱신한다.

---

# Real Korea Travel

외국인 관광객이 **한국인처럼 실패 없이 맛집과 카페를 선택**하도록 돕는 로컬 여행 플랫폼 (MVP).

> **Travel Korea Like a Local.** 한국인처럼 여행하는 가장 쉬운 방법

## 핵심 아이디어
- 외국인이 "한국인이라면 여기서 뭘 선택할까?"에 답을 주는 **의사결정 플랫폼**
- Google 평점만으로 신뢰 불가 → **Local Score**로 해결
- 영어 메뉴, 카드 결제, 혼밥 여부, 웨이팅, 추천 메뉴 등 방문 전 필요한 정보 제공

## MVP 범위
- **지역**: 서울 (성수, 홍대, 강남, 명동)
- **카테고리**: 맛집, 카페, 디저트, 술집
- **기능**: 장소 탐색·상세, 검색/필터, Local Score, AI 리뷰 요약, 즐겨찾기, Google Maps 길찾기
- **제외**: 리뷰 작성, SNS, 일정 자동 생성, 예약/결제

## 성공 기준 (MVP)
|지표|목표|
|---|---|
|등록 장소|200개 이상|
|장소 상세 조회|월 10,000회 이상|
|즐겨찾기 등록|1,000건 이상|
|사용자 재방문율|30% 이상|
|사용자 만족도|4.5 / 5 이상|

## MVP API 범위 (OpenAPI SSOT)
- 현재 구현 인증: `GET /auth/oauth2/google`, `GET /auth/oauth2/google/callback`, `POST /api/v1/auth/refresh`
- 현재 구현 장소: `GET /api/v1/places`, `GET /api/v1/places/{placeId}`
- 현재 구현 검색: `GET /api/v1/search`
- 현재 구현 즐겨찾기: `POST /api/v1/bookmarks/toggle`, `GET /api/v1/bookmarks`
- 현재 시드 데이터: 서울·부산·제주 주요 세부 지역 10곳, 장소 200개 및 기본 Local Score·PlaceFeature
- 후속 구현 범위: Local Score 고도화, AI 리뷰 요약, 이미지·운영시간·Local Tip

## 관계
- 프로젝트 루트: `C:\Users\alche\OneDrive\문서\Default Project\Real_Korea_Travel`
- Jira: 프로젝트 `RKT`, 보드 id 67, Epic RKT-1~7, Story RKT-8~32, 버전 `0.1.0 MVP`
