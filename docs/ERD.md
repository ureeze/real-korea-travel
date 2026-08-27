# ERD 요약 — Real Korea Travel

> 이 문서는 AI Agent와 코드 리뷰를 위한 로컬 Context/Cache다.
> DB 설계의 Source of Truth는 [Notion ERD](https://app.notion.com/p/ERD-3b38a222ce3c804da4e6d8165ad1fbfb)이며, 실제 적용 스키마는 Flyway Migration을 기준으로 한다.
> 상세 컬럼·제약·설계 결정은 Notion ERD에서 확인한다.

## 기준 문서

- 설계 기준: [Notion ERD](https://app.notion.com/p/ERD-3b38a222ce3c804da4e6d8165ad1fbfb)
- 실제 DB 적용 기준: `backend/src/main/resources/db/migration/`
- 관련 Entity 구현: `backend/src/main/java/com/realkoreatravel/*/domain/`

## 테이블 요약

| 테이블 | 역할 | 현재 Java/API 구현 |
|---|---|---|
| `member` | OAuth 회원 | 구현 |
| `region` | 장소 지역 및 자기참조 계층 | 구현 |
| `category` | 장소 카테고리 | 구현 |
| `place` | 장소 기본 정보 | 구현 |
| `place_feature` | 외국인 편의 정보 | 구현 |
| `menu` | 장소 메뉴 | 구현 |
| `opening_hour` | 요일별 운영시간 | DB 설계·Migration 포함, API 후속 |
| `place_image` | 장소 이미지 | DB 설계·Migration 포함, API 후속 |
| `local_score` | 현지인 점수 | Entity·Repository 구현, API 후속 |
| `ai_review_summary` | AI 리뷰 요약 | DB 설계·Migration 포함, API 후속 |
| `local_tip` | 현지인 팁 | DB 설계·Migration 포함, API 후속 |
| `bookmark` | 회원별 즐겨찾기 | Entity·Repository·등록 API 구현 |
| `review` | 외부 리뷰 수집 원본 | DB 설계·Migration 포함, 외부 API 후속 |

## 핵심 관계

```text
REGION 1:N PLACE
CATEGORY 1:N PLACE
PLACE 1:1 PLACE_FEATURE
PLACE 1:N MENU
PLACE 1:N OPENING_HOUR
PLACE 1:N PLACE_IMAGE
PLACE 1:1 LOCAL_SCORE
PLACE 1:N AI_REVIEW_SUMMARY
PLACE 1:N LOCAL_TIP
PLACE 1:N REVIEW
MEMBER 1:N BOOKMARK
PLACE 1:N BOOKMARK
REGION 1:N REGION (자기참조)
```

## 정합성 규칙

- Notion ERD를 수정하면 이 요약의 테이블·관계·구현 상태도 함께 확인한다.
- DB 컬럼이나 제약을 변경할 때는 기존 Migration을 수정하지 않고 새 Flyway Migration을 추가한다.
- Migration과 Notion ERD가 다르면 실제 적용 여부는 Migration, 설계 의도는 Notion ERD에서 확인한 뒤 ADR 또는 결정 기록을 갱신한다.
- 이 파일에 상세 컬럼을 복사해 추가하지 않는다. 상세 설계가 필요하면 Notion ERD를 갱신한다.
- `local_score`는 `place_id` UNIQUE 제약으로 Place와 1:1 관계를 유지하며, Local Score API는 후속 티켓에서 구현한다.
- 현재 Entity 매핑은 `LocalScore → Place` 단방향으로 유지해 Place 조회 시 Local Score의 비소유자 측 지연 로딩 문제를 피한다.
- `place_feature`는 `place_id` UNIQUE 제약을 가진 `PlaceFeature → Place` 단방향 Entity 매핑을 사용하고, 장소 상세 조회 시 전용 Repository로 편의정보를 조회한다.
