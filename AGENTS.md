# Real Korea Travel

이 저장소는 Real Korea Travel MVP 개발을 위한 작업 공간이다.

Real Korea Travel은 외국인 관광객이 **한국인처럼 실패 없이 맛집과 카페를 선택할 수 있도록** 돕는 로컬 여행 플랫폼이다. 외국인이 '한국인이라면 어디를 선택할까?'라는 질문에 답을 주는 것을 목표로 한다.

> **Travel Korea Like a Local.** 한국인처럼 여행하는 가장 쉬운 방법

## 역할

- AI 개발 에이전트는 Real Korea Travel MVP 개발을 돕는다.
- Jira 티켓과 Notion 기준 문서와 Memory Bank를 바탕으로 작업 범위, 구현 방향, 검증 방법을 정한다.
- 구현 작업은 구현, 테스트/검증, Memory Bank 갱신까지 완료한다.

## 작업 흐름

```text
Jira 작업 시작
→ Jira: 진행 중
→ feature/{JiraKey}-{short-summary} 브랜치
→ 구현 + 테스트
→ 커밋
→ PR 생성
→ Jira: 검토 중
→ 사용자가 PR Merge
→ GitHub ↔ Jira 자동화로 Jira: 완료
→ Jira가 완료인지 확인 후에만 다음 티켓 진행
```

## 상태 관리 원칙

- **Jira를 작업 상태의 Source of Truth로 사용한다.**
- Jira 상태는 `진행 중 → 검토 중 → 완료` 흐름으로 관리한다.
- PR Merge 후 Jira가 `완료`이면 작업 완료로 판단한다.
- 다음 작업 시작 시 Jira가 `완료`인지 확인하면 되며, 매번 GitHub PR의 Merge 여부까지 별도로 확인하지 않는다.
- `검토 중` 상태의 PR이 머지 전이라면 다음 티켓을 제안하거나 착수하지 않는다. 다음 티켓은 직전 PR이 머지되어 Jira가 `완료`가 된 뒤에만 진행한다.
- Jira와 GitHub 상태가 불일치하거나 실제 확인이 필요한 경우에만 GitHub PR 상태를 확인한다.
- `1 Jira = 1 PR`을 기본으로 하되, 하나에 여러 논리적 커밋이 포함되는 것은 허용한다.

## 작업 원칙

- 작업 시작 전에 현재 작업의 Jira 티켓과 관련 Memory Bank 문서를 필요한 만큼 읽는다.
- Jira 티켓이 있는 작업은 Jira Key를 커밋/PR 설명에 함께 남긴다.
- 구현 변경이 있으면 테스트 또는 검증 방법을 함께 수행한다.
- 구현 작업 완료 후 결과를 보고할 때는 파일 경로와 클래스명을 포함해 파일·클래스별 변경사항, 관련 endpoint, 테스트 및 검증 결과를 함께 정리한다.
- 구현 작업으로 새로 만들거나 수정한 메서드에는 메서드가 수행하는 핵심 기능을 설명하는 주석을 작성한다. 단순한 문법 설명보다 구현 목적과 동작을 중심으로 작성한다.
- 구현 작업은 구현 → 테스트/검증 → 변경사항 보고 단계까지만 진행하고, 사용자가 별도로 승인하기 전에는 `git add`, `git commit`, `git push`, PR 생성을 수행하지 않는다.

## 실행 전 확인 원칙

코드 수정, 파일 생성/삭제, 구조 변경, Git 작업처럼 프로젝트 상태를 변경하는 작업은 실행 전에 실행계획을 먼저 보여주고 확인을 받는다.

실행.계획에는 다음 내용을 포함한다.

- 작업 목표
- 수정 또는 생성할 파일
- 예상 변경 내용
- 검증 방법
- Memory Bank 갱신 여부
- 관련 Jira Key

단순 조회, 검색, 문서 확인, 코드 분석, 테스트 실행처럼 상태를 변경하지 않는 작업은 별도 확인 없이 수행할 수 있다.

## 프로젝트 시작 규칙

1. 현재 작업의 Jira Key와 관련 문서를 확인한다.
2. 필요한 경우 `memory-bank/project-brief.md`, `memory-bank/tech-stack.md`, `memory-bank/coding-rules.md`, `memory-bank/decisions.md`를 읽는다.
3. 변경 범위, 검증 방법, 자동화 범위를 정한다.
4. 상태 변경 작업이면 Git 규칙에 따라 저장소와 브랜치를 확인한다.

## Memory Bank

- Memory Bank는 프로젝트 판단 기준을 저장하는 로컬 문서 집합이다.
- Memory Bank 구조와 각 문서의 역할은 `memory-bank/project-brief.md`를 기준으로 한다.
- 작업 중 발견한 기준, 결정, 기술 선택이 있으면 작업 종료 시 Memory Bank를 갱신한다.

## 문서 우선순위

- 제품 요구사항과 설계 기준은 Notion 문서를 우선한다.
- Notion 문서와 Memory Bank가 충돌하면 Notion 문서를 기준으로 판단하고, 작업 종료 후 Memory Bank를 갱신한다.
- API 엔드포인트의 최종 권위(SSOT)는 **OpenAPI 명세 문서**를 기준으로 한다.

## 작업 자동화 범위

- 로컬 구현만 수행
- 로컬 구현 + 테스트 + Memory Bank 갱신
- 로컬 구현 + 테스트 + Memory Bank 갱신 + GitHub PR 생성
- 로컬 구현 + 테스트 + Memory Bank 갱신 + GitHub PR 생성 + Jira 상태 변경

기본값은 `로컬 구현 + 테스트 (+ 필요 시에만 Memory Bank 갱신)`이다. GitHub push와 PR 생성은 실행계획에 포함하고 확인을 받은 경우에만 수행한다. Jira 상태 변경은 Jira 규칙의 기본 전환 기준을 따른다.

## Git 규칙

- 상태 변경 작업 시작 전에 `git status --short --branch`로 Git 저장소와 브랜치를 확인한다.
- 브랜치 전략은 GitHub Flow를 따른다.
- `main`은 배포 · 릴리스 기준 브랜치로 유지한다.
- Jira 티켓 작업 브랜치는 `main`에서 분기한다.
- Jira 티켓 작업 브랜치는 `feature/{JiraKey}-{short-summary}` 형식을 기본으로 한다. 예: `feature/RKT-9-spring-boot-bootstrap`
- 완료 후 PR은 작업 브랜치에서 `main`으로 보낸다.
- 커밋 메시지는 개발 커밋과 PR 제목을 구분해 작성한다.
  - 개발 커밋: `{type}({scope}): {설명}` 형식. 예: `feat(place): 장소 목록 API 구현`
  - PR 제목: `{JiraKey} {설명}` 형식. 예: `RKT-18 장소 목록 API 구현` (squad merge 시 `(#N)` 자동 추가)
  - 개발 커밋에는 Jira Key를 붙이지 않고, PR 제목에만 단다.
  - type 목록: `feat`, `fix`, `docs`, `ci`, `refactor`, `test`, `chore`, `build`, `style`
  - scope 목록: class, domain 모듈의 경우 `auth`, `member`, `place`, `search`, `local-score`, `bookmark`, `ai-summary`, `common`, 그 외 메타는 `docs`, `ci`, `build`, `test`
  - PR 제목에는 Conventional prefix(`feat:` 등)를 붙이지 않는다.

## Jira 규칙

- Jira 프로젝트 key는 `RKT`이다.
- 개발 작업은 Jira 티켓 단위로 진행한다.
- 기능/코드 작업(엔드포인트·엔티티·로직·리팩터링·버그 수정)은 대응하는 티켓이 없으면 **착수 전에 Jira 티켓을 먼저 생성한다**. 순수 문서·CI·도구 같은 메타 작업은 로컬 T-ID로 관리할 수 있다.
- 신규 티켓 생성은 실행계획에 포함해 확인받은 뒤 해당 도메인 에픽 하위에 만들고, 생성된 Jira Key로 착수(`진행 중` 전환 + `feature/{JiraKey}-{short-summary}` 브랜치)한다.
- 티켓 타입 컨벤션: 사용자가 직접 쓰는 기능 단위(공개 API·화면)는 `스토리(Story)`, 기술·인프라·워커·테스트·설정 작업은 `작업(Task)`로.
- 작업 시작 시 Jira 티켓을 확인한다.
- 작업 브랜치를 생성하거나 구현에 착수하면 Jira 상태를 `진행 중`으로 기본 전환한다.
- GitHub PR 생성 시 Jira 티켓에 PR 링크와 검증 결과를 댓글로 남기고 상태를 `검토 중`으로 변경한다.
- PR이 `main`에 merge되면 GitHub for Jira 연동이 자동으로 `완료`로 전환한다.
- `검토 중` 전환 후에는 PR 머지 전까지 추가 티켓 작업을 시작하지 않는다. Jira가 `완료`가 되면 다음 티켓을 제안한다.
- Jira 댓글에는 작업 요약, 검증 결과, PR 링크를 남긴다.

## GitHub PR 규칙

- PR 대상 브랜치는 GitHub Flow 기준을 따른다.
- PR 제목과 본문에 Jira Key, 변경 요약, 검증 결과를 포함한다.
- PR 본문의 변경 내용은 기능 단위와 변경 파일·클래스 단위로 구분해 작성한다.
- 파일·클래스별 항목은 `#### 파일 경로 - 클래스명` 형식으로 한 줄에 통합해 표기하고, 핵심 변경 사항과 관련 endpoint를 함께 기록한다.
- 테스트 파일은 별도 섹션으로 분리하고 검증 대상과 결과를 작성한다.
- 설정·문서·Migration 변경은 별도 섹션으로 구분한다.
- PR 본문은 `변경 내용 → 파일/클래스별 상세 → 테스트 → 설정/문서` 순서로 작성한다.
- GitHub push와 PR 생성은 사용자가 요청하거나 실행계획에 포함되어 확인된 경우에만 수행한다.
- PR merge는 사용자가 GitHub UI에서 직접 수행한다. 에이전트는 명시적 요청이 아니라면 API/CLI로 merge하지 않는다.

## Slack 공유 규칙

- Slack 알림은 GitHub · Jira 공식 Slack 앱이 담당한다. 직접 Slack 메시지를 전송하지 않는다.

## 테스트 규칙

- 구현 변경이 있으면 관련 테스트 또는 검증 방법을 함께 수행한다.
- Java/Spring 테스트는 JUnit 5와 Spring Boot Test를 기준으로 한다.
- DB 제약, Migration, 동시성은 통합 테스트로 검증한다.

## 배포 규칙

- 현재 프로젝트는 초기 로컬 개발 기준이다.
- 배포, 운영 환경 변경, 환경변수 변경, DB migration 적용은 실행계획에 포함하고 확인을 받은 경우에만 수행한다.
- 적용된 Flyway Migration은 수정하지 않는다. 변경은 새 Migration으로 추가한다.

## 코딩 규칙

- Notion PRD/API/ERD의 용어를 코드와 문서에 일관되게 사용한다.
- 현재 기준 용어는 `member`, `place`, `local-score`, `place-feature`, `bookmark`이다.
- **URL 파라키터**(`region`, `category` 등)는 **소문자**를 사용하고, 응답 JSON의 `code` 필드는 **대문자**를 사용한다.
- Controller, Service, Repository 책임을 분리한다.
- API DTO와 Entity를 직접 공유하지 않는다.
- 상태값은 Java enum으로 표현하되 DB CHECK 값과 반드시 일치시킨다.

## 기준 문서 (Notion)

- PRD: `https://app.notion.com/p/PRD-3b38a222ce3c80ca8fe5f7aec346a322`
- ERD/DB 설계서: `https://app.notion.com/p/ERD-3b38a222ce3c804da4e6d8165ad1fbfb`
- API 명세서(OpenAPI): `https://app.notion.com/p/OpenAPI-3b48a222ce3c811b82a2f170c5645051`
- Jira 프로젝트: `https://ureeze.atlassian.net/jira/software/projects/RKT/boards/67`

## 작업 종료 체크리스트

- [ ] 요청한 작업을 완료했다.
- [ ] 관련 테스트 또는 검증을 수행했다.
- [ ] 프로젝트 상태 변경 작업이면 Git 저장소와 브랜치 상태를 확인했다.
- [ ] 필요한 경우 Notion/Jira와 용어가 일치하는지 확인했다.
- [ ] 관련 커밋을 남기고 PR 생성 여부를 사용자와 확인했다.

## 개발 우선순위

- MVP 도메인 구현 순서는 `인프라 → 인증 → 장소 → 시드 데이터 → Local Score → 즐겨찾기 → AI 리뷰 요약`을 따른다.
- 구체적인 티켓과 진행 상태는 Jira 백로그를 기준으로 한다.

## 금지 사항

- PRD/API/ERD 기준과 다른 용어를 임의로 도입하지 않는다.
- 이전 용어(`booking_provider` 등)를 새 코드에 도입하지 않는다.
- 민감정보, JWT, 개인정보, 원문 프롬프트를 로그나 audit metadata에 저장하지 않는다.
- 적용된 Flyway Migration을 수정하지 않는다. 변경은 새 Migration으로 추가한다.
- 외부 API, LLM, Kafka 호출을 DB 트랜잭션 안에서 수행하지 않는다.
