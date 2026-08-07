# Coding Rules — Real Korea Travel

Notion PRD/API/ERD의 용어를 코드와 문서에 일관되게 사용한다.

## 표기 규칙 (중요)
- **URL 파라미터**(`region`, `category` 등)는 **소문자** 사용. 예: `region=seongsu`
- 응답 JSON 의 `code` 필드는 **대문자** 사용.
- 문서(PRD/OpenAPI)와 코드 모두 동일 규칙 적용.

## 기준 용어
- `member` (회원)
- `place` (장소)
- `local-score` (현지인 점수)
- `place-feature` (장소 편의정보)
- `bookmark` (즐겨찾기)
- (확장 예정) `ai-review-summary`, `region`, `category`

> 이전 프로젝트 용어(`booking_provider`, `slot` 등)는 도입하지 않는다.

## 아키텍처
- Controller, Service, Repository 책임 분리.
- API DTO 와 Entity 를 직접 공유하지 않는다.
- 상태값은 Java enum 으로 표현하되 DB CHECK 값과 일치시킨다.

## 커밋 컨벤션
- 개발 커밋: `{type}({scope}): {설명}`. 예: `feat(place): 장소 목록 API 구현`
- PR 제목: `{JiraKey} {설명}`. 예: `RKT-18 장소 목록 API 구현`
- 개발 커밋에 Jira Key 는 붙이지 않고, PR 제목에만 단다.
- type: `feat`, `fix`, `docs`, `ci`, `refactor`, `test`, `chore`, `build`, `style`
- scope(도메인): `auth`, `member`, `place`, `search`, `local-score`, `bookmark`, `ai-summary`, `common`
- scope(메타): `docs`, `ci`, `build`, `test`

## 테스트
- JUnit 5 + Spring Boot Test.
- DB 제약, Migration, 동시성은 통합 테스트로 검증.