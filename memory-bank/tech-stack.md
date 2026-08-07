# Tech Stack — Real Korea Travel

> 주의: 아래는 **계획치**다. 실제 최종 버전은 **E1 인프라 (RKT-8 기술 스택 버전 확정)** 에서 확정한다. 확정 후 아래 표를 업데이트한다.

## 계획 스택

|영역|기술|버전(계획)|확정 여부|
|---|---|---|---|
|언어|Java|25 (LTS)|⏳ E1 확정|
|프레임워크|Spring Boot|4.x (Latest Stable)|⏳ E1 확정|
|ORM|Spring Data JPA|-|⏳ E1 확정|
|보안|Spring Security|현재 기준|⏳ E1 확정|
|DB|PostgreSQL|18|⏳ E1 확정|
|Cache|Redis|8|⏳ E1 확정|
|AI|GPT API|5.5 모델|P1 스케줄|
|Search|PostgreSQL Full Text Search|-|MVP 사용|
|Map|Google Maps SDK / Places API|외부 연동|구현 시|
|Storage|Amazon S3|외부 연동|구현 시|
|Infra|Docker + AWS(ECS, RDS, CloudFront)|외부|E1/E13|

## 확정 원칙
- 버전은 **문서에 계획치로,** 실제 최신 안정 호환 조합은 **E1 단계에서 확정**한다.
- Spring Boot ↔ Java, JPA ↔ PostgreSQL 호환성을 함께 검증한다.