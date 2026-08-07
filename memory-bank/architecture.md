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

## 데이터 모델 요약 (ERD 기준)
- `member`, `place`, `local_score`, `place_feature`, `bookmark`
- `region`, `category` 는 place 의 참조(FK).
- (확장) `place_image`, `menu`, `ai_review_summary`, `local_tip`

## 배포 (계획, E1/E13)
- Docker → AWS ECS (컨테이너), AWS RDS (PostgreSQL), AWS CloudFront (CDN)