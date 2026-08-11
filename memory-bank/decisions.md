# Decisions — Real Korea Travel

> ADR 인덱스(요약 캐시). 원본은 Notion ADR(있으면)을 기준으로 하거나, 여기에 기록한다. 결정이 생기면 여기에 추가한다.

## ADR 목록

|#|날짜|결정|상태|근거|
|---|---|---|---|---|
|-|-|(아직 기록된 ADR 없음)|-|-|

## 진행 중 결정 사항 (미확정)
- [ ] **Memory Bank 도입** — 2026-08-08 도입 결정함. 구조는 `project-brief.md` 기준. (ADR 화 필요)
- [x] **기술 버전 확정** — Java 25 (LTS) / Spring Boot 4.1.0 / PostgreSQL 18.4 / Redis 8.10.0. RKT-8에서 확정 (2026-08-09).

## 잠정 합의 (현재까지)
- URL 파라미터는 소문자, 응답 `code` 는 대문자 표기.
- 홈/메타 API(`/home`, `/regions`, `/categories`)는 MVP 에서 제외.
- 문서 SSOT 는 **OpenAPI 명세** (PRD 는 요약 역할).
- Jira 워크플로: `진행 중 → 검토 중 → 완료`.
- **티켓 게이트(2026-08-11)**: `검토 중` 상태의 PR이 머지 전이면 다음 티켓을 제안/착수하지 않는다. 다음 티켓은 직전 PR이 머지되어 Jira가 `완료`가 된 뒤에만 진행한다. (AGENTS.md 반영)
- 프로젝트: Jira `RKT`, 버전 `0.1.0 MVP`.
- 기술 스택 확정: Java 25 (LTS) / Spring Boot 4.1.0 / PostgreSQL 18.4 / Redis 8.10.0 (RKT-8, 2026-08-09).
- Slack 알림은 Jira · GitHub 공식 Slack 앱을 사용한다(직접 메시지 전송 안 함). `#real-korea-travel-dev` 채널 구독.
  - Jira → Slack: 채널에서 `/jira connect`로 사이트 연결 + 프로젝트(RKT) 채널 구독. 상태 변경 이벤트가 채널에 도착하는 것 확인(2026-08-09).
  - GitHub → Slack: GitHub Slack 앱으로 PR 생성 등 이벤트를 채널에 알림. PR opened 알림 도착 확인(2026-08-09).