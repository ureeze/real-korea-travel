# ADR-0002: Local Score 점수 산정 정책

- 상태: Accepted
- 작성일: 2026-08-25
- 관련 Jira: RKT-23

## Context

LocalScore는 음식, 가격, 분위기, 재방문, 현지인 추천의 다섯 가지 세부 점수와 이를 종합한 `total_score`를 가진다. Jira와 Notion PRD는 100점 만점과 다섯 가지 구성 요소를 정의하지만, 각 요소의 구체적인 숫자 가중치와 재계산 정책은 정의하지 않았다.

## Decision

MVP에서는 다섯 가지 세부 점수에 동일한 20% 가중치를 적용한다.

```text
totalScore = (foodScore + priceScore + atmosphereScore + revisitScore + localRecommendScore) / 5
```

- 모든 입력 점수는 0 이상 100 이하만 허용한다.
- 종합 점수의 소수점은 `HALF_UP` 방식으로 반올림해 `Integer`로 저장한다.
- 점수 계산은 별도의 `LocalScoreCalculator`에서 담당한다.
- LocalScore의 상태 변경과 `updatedAt` 갱신은 엔티티 메서드를 통해 수행한다.
- LocalScore 조회·재계산·저장 흐름은 `LocalScoreService`에서 담당한다.
- 다섯 점수 중 하나라도 null이면 종합 점수를 계산하지 않고 입력 오류로 처리한다.

## Consequences

- 점수 계산 규칙을 서비스 로직과 분리해 단위 테스트와 향후 가중치 변경이 쉬워진다.
- MVP에서 모든 평가 요소가 동일하게 종합 점수에 반영된다.
- 가중치를 변경하거나 점수 산정 근거를 고도화할 때는 이 ADR과 테스트를 함께 갱신해야 한다.
- 현재 작업에서는 Local Score API를 추가하지 않고 내부 재계산 서비스까지만 구현한다.

## Follow-up

- 실제 운영 데이터가 쌓이면 요소별 중요도와 가중치를 재검토한다.
- 가중치 변경 시 기존 점수의 일괄 재계산과 Migration 필요 여부를 검토한다.
- 상세 응답에 Local Score를 포함하는 작업은 RKT-24에서 진행한다.
