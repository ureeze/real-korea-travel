# Architecture — Real Korea Travel

> 시스템 구성과 모듈/패키지 구조 가이드. 세부 구조는 E1 (RKT-9 프로젝트 생성 & 모듈 구조) 에서 확정한다.

## 시스템 구성 (계획)
```text
[App] (추후)
  │
▼ REST API (Spring Boot + Java)
  ├─ JWT 인증 (Google OAuth)
  ↓
 [PostgreSQL 18]  ← 장소/점수/즐겨찾기/회원 (Flyway migration)
 [Redis 8]        ← 인기 장소/검색/Local Score 캐싱
 [GPT API]        ← AI 리뷰 요약 (P1)
 [Google Maps/Places] ← 지도 길찾기/위치
 [Amazon S3]      ← 장소 이미지
```

## 백엔드 패키지 구조 (제안, E1 확정)
```
com.realkoreatravel
├── auth        (인증, JWT, Security)
├── member
├── place       (장소, 상세)
├── search      (FTS 키워드 검색)
├── localscore  (Local Score)
├── bookmark    (즐겨찾기)
├── aisummary   (AI 리뷰 요약)   # P1
└── common      (공통 응답/예외)
```
각 패키지는: `controller / service / repository / domain(엔티티) / dto`

인증 요청은 `auth.jwt.JwtAuthenticationFilter`가 Bearer access token을 검증하고,
검증된 회원 ID를 Spring Security 인증 객체에 등록한다. Refresh token은
`POST /api/v1/auth/refresh`에서 검증한 뒤 새 access/refresh token 쌍으로 교체한다.

현재 Google OAuth 진입점은 `GET /auth/oauth2/google`이며, Google callback
`GET /auth/oauth2/google/callback`에서 회원 조회·생성 후 JWT를 발급한다. Access token 기본 만료시간은 3600초(1시간)다.

## 데이터 모델 요약 (Notion ERD 기준)
- `member`, `place`, `region`, `category`, `place_feature`, `menu`
- `region`, `category` 는 place 의 참조(FK)이며, `place_feature`는 1:1, `menu`는 1:N 관계다.
- (DB 설계/후속 구현) `local_score`, `opening_hour`, `place_image`, `ai_review_summary`, `local_tip`, `review`; `bookmark` 등록·토글 API 구현
- (RKT-11, 2026-08-11) 초기 스키마 13개 테이블을 Flyway V1/V2로 생성 완료. JPA 엔티티는 도메인 구현 티켓에서 작성.
- 상세 컬럼은 Notion ERD, 실제 적용 여부는 `backend/src/main/resources/db/migration/`, AI Agent용 관계 요약은 `docs/ERD.md`를 확인한다.

## 배포 (계획, E1/E13)
- Docker → AWS ECS (컨테이너), AWS RDS (PostgreSQL), AWS CloudFront (CDN)
