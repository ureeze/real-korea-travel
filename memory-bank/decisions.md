# Decisions — Real Korea Travel

> ADR 인덱스(요약 캐시). 원본은 Notion ADR(있으면)을 기준으로 하거나, 여기에 기록한다. 결정이 생기면 여기에 추가한다.

## ADR 목록

|#|날짜|결정|상태|근거|
|---|---|---|---|---|
|-|-|(아직 기록된 ADR 없음)|-|-|

## 진행 중 결정 사항 (미확정)
- [ ] **Memory Bank 도입** — 2026-08-08 도입 결정함. 구조는 `project-brief.md` 기준. (ADR 화 필요)
- [x] **기술 버전 확정** — Java 25 (LTS) / Spring Boot 4.1.0 / PostgreSQL 18.4 / Redis 8.10.0. RKT-8에서 확정 (2026-08-09).
- [x] **JWT 인증 토큰** — RKT-15에서 access token과 refresh token을 HMAC 서명 JWT로 발급한다. access token은 Bearer 인증에 사용하고, refresh token은 `/api/v1/auth/refresh`에서 새 토큰 쌍으로 교체한다. secret과 만료 시간은 환경변수로 주입한다 (2026-08-15).
- [x] **토큰 갱신 API** — RKT-16에서 refresh token의 서명·만료·타입을 검증하고, 유효하지 않은 토큰은 `401 INVALID_REFRESH_TOKEN`으로 응답한다. 정상 요청은 새 access/refresh token 쌍을 반환한다 (2026-08-15).
- [x] **Spring Security API 보호** — RKT-17에서 JWT 기반 Stateless 세션을 사용하고, 인증 필요 API는 access token을 검증한다. 공개 엔드포인트는 별도로 허용하며 인증 실패는 `401`, 인가 실패는 `403`으로 응답한다 (2026-08-16).
- [x] **장소 목록 API 조회 정책** — RKT-18에서 `region`·`category` 소문자 코드로 장소를 필터링하고, `page`·`size`·`sort`로 페이징과 정렬을 지원한다. `ACTIVE` 상태이며 삭제되지 않은 장소만 반환한다 (2026-08-16).
- [x] **장소 상세 조회 정책** — RKT-19에서 `ACTIVE` 상태이며 삭제되지 않은 장소만 ID로 조회하고, 장소 기본 정보와 `PlaceFeature`·메뉴 목록을 함께 반환한다. 메뉴는 `sort_order`와 ID 오름차순으로 정렬하며 Local Score는 별도 모델 작업에서 연계한다 (2026-08-18).
- [x] **일반 수치 타입 정책** — 특별한 저장 공간 최적화 이유가 없는 단순 수치 필드는 Java `Short`와 PostgreSQL `SMALLINT` 대신 `Integer`와 `INTEGER`를 우선 사용한다. RKT-19에서 `PlaceFeature.avgWaitTimeMin`에 적용했다 (2026-08-18).

## 잠정 합의 (현재까지)
- URL 파라미터는 소문자, 응답 `code` 는 대문자 표기.
- 홈/메타 API(`/home`, `/regions`, `/categories`)는 MVP 에서 제외.
- 문서 SSOT 는 **OpenAPI 명세** (PRD 는 요약 역할).
- Jira 워크플로: `진행 중 → 검토 중 → 완료`.
- **티켓 게이트(2026-08-11)**: `검토 중` 상태의 PR이 머지 전이면 다음 티켓을 제안/착수하지 않는다. 다음 티켓은 직전 PR이 머지되어 Jira가 `완료`가 된 뒤에만 진행한다. (AGENTS.md 반영)
- **배포 후순위 (2026-08-13)**: RKT-13 (AWS 배포, ECS/RDS/CloudFront)은 MVP 기능 개발 완료 후 진행. priority를 High → Low로 조정했고, RKT-14 (Google OAuth)부터 도메인 기능 티켓을 우선 진행한다.
- 프로젝트: Jira `RKT`, 버전 `0.1.0 MVP`.
- 기술 스택 확정: Java 25 (LTS) / Spring Boot 4.1.0 / PostgreSQL 18.4 / Redis 8.10.0 (RKT-8, 2026-08-09).
- Slack 알림은 Jira · GitHub 공식 Slack 앱을 사용한다(직접 메시지 전송 안 함). `#real-korea-travel-dev` 채널 구독.
  - Jira → Slack: 채널에서 `/jira connect`로 사이트 연결 + 프로젝트(RKT) 채널 구독. 상태 변경 이벤트가 채널에 도착하는 것 확인(2026-08-09).
  - GitHub → Slack: GitHub Slack 앱으로 PR 생성 등 이벤트를 채널에 알림. PR opened 알림 도착 확인(2026-08-09).
