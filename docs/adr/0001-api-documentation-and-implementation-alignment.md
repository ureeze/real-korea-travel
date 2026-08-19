# ADR-0001: API 문서와 실제 구현의 정합성 관리

- Status: Accepted
- Date: 2026-08-19
- Decision timing: 기존 구현과 문서의 불일치를 확인한 후 소급 기록

## Context

프로젝트의 요구사항·설계·API 문서가 Notion, `docs/`, `memory-bank/`에 나뉘어 있고, 코드와 Migration은 별도로 변경된다. 그 결과 다음과 같은 불일치가 발생했다.

- Google OAuth 문서 경로와 실제 Controller 경로가 달랐다.
- 장소 목록·상세 응답 필드가 실제 DTO와 달랐다.
- ERD에 정의된 컬럼이 Flyway Migration에는 아직 반영되지 않은 경우가 있었다.
- 구현된 기능과 구현 예정 기능이 같은 문서 안에 함께 표현되어 현재 상태를 오해할 수 있었다.

## Decision

1. 정식 기술 의사결정은 저장소의 `docs/adr/`에 Markdown ADR로 기록한다.
2. Notion은 팀 공유용 요약과 요구사항·설계 문서로 사용한다.
3. `memory-bank/decisions.md`는 ADR 인덱스와 핵심 결정 요약만 관리한다.
4. API 문서에는 `현재 구현`과 `구현 예정`을 명시적으로 구분한다.
5. API 변경 시 Controller·DTO·테스트·OpenAPI를 함께 대조한다.
6. DB 변경 시 Flyway Migration·ERD·Entity를 함께 대조한다.
7. ADR은 구현 전에 작성하는 것을 원칙으로 하되, 이미 내려진 중요한 결정은 현재 시점에 소급하여 기록할 수 있다.

## Consequences

### Positive

- 기술 결정의 배경과 대안을 Git 이력과 함께 확인할 수 있다.
- Notion과 코드의 역할이 분리되어 문서의 책임 범위가 명확해진다.
- 구현 상태와 제품 로드맵을 구분할 수 있다.

### Negative

- API·DB 구조를 변경할 때 확인해야 할 문서가 늘어난다.
- 중요한 변경마다 ADR과 Notion 요약을 함께 갱신해야 한다.

## Follow-up

- 새 기술적 결정은 `docs/adr/000N-short-title.md` 형식으로 작성한다.
- 결정이 변경되면 기존 ADR을 삭제하지 않고 상태를 `Superseded`로 변경한 뒤 새 ADR을 추가한다.
- 구현 작업 종료 시 관련 ADR, Notion, Memory Bank의 정합성을 확인한다.
