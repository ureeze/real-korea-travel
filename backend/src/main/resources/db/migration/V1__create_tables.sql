-- RKT-11 초기 스키마 (ERD v0.1 기준)
-- 13개 테이블: member, region, category, place, opening_hour, place_image,
--             menu, local_score, place_feature, ai_review_summary, local_tip, bookmark, review
-- member — 회원
CREATE TABLE member (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email              VARCHAR(255)  NOT NULL,
    nickname           VARCHAR(50),
    profile_image_url  VARCHAR(500),
    language           VARCHAR(10)   DEFAULT 'en' NOT NULL,
    provider           VARCHAR(20)   DEFAULT 'GOOGLE' NOT NULL,
    provider_id        VARCHAR(255)  NOT NULL,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uk_member_email UNIQUE (email),
    CONSTRAINT uk_member_provider_id UNIQUE (provider, provider_id)
);

-- region — 지역 (계층형, 부모는 도시)
CREATE TABLE region (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_id      BIGINT REFERENCES region (id),
    name           VARCHAR(50) NOT NULL,
    code           VARCHAR(50) NOT NULL,
    display_order  INT         NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_region_code UNIQUE (code)
);

-- category — 카테고리
CREATE TABLE category (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(50) NOT NULL,
    code           VARCHAR(50) NOT NULL,
    display_order  INT         NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_category_code UNIQUE (code)
);

-- place — 장소
CREATE TABLE place (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    region_id        BIGINT       NOT NULL REFERENCES region (id),
    category_id      BIGINT       NOT NULL REFERENCES category (id),
    name             VARCHAR(100) NOT NULL,
    address          VARCHAR(255) NOT NULL,
    latitude         NUMERIC(10, 7),
    longitude        NUMERIC(10, 7),
    phone            VARCHAR(30),
    price_level      SMALLINT CHECK (price_level BETWEEN 1 AND 4),
    description      TEXT,
    google_place_id  VARCHAR(255),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at       TIMESTAMPTZ,
    CONSTRAINT uk_place_google_place_id UNIQUE (google_place_id),
    CONSTRAINT ck_place_status CHECK (status IN ('ACTIVE', 'CLOSED', 'HIDDEN'))
);

-- place 조회 인덱스는 도메인 구현 티켓(RKT-18~20)에서 필요 시 추가

-- opening_hour — 운영시간 (요일별, 시간대 분리 시 여러 행 허용)
CREATE TABLE opening_hour (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id     BIGINT      NOT NULL REFERENCES place (id),
    day_of_week  SMALLINT    NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    open_time    TIME        NOT NULL,
    close_time   TIME        NOT NULL,
    is_closed    BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_opening_hour_time CHECK (close_time > open_time),
    CONSTRAINT uk_opening_hour_place_day_time UNIQUE (place_id, day_of_week, open_time, close_time)
);

-- place_image — 장소 이미지
CREATE TABLE place_image (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id    BIGINT       NOT NULL REFERENCES place (id),
    image_url   VARCHAR(500) NOT NULL,
    is_main     BOOLEAN      NOT NULL DEFAULT FALSE,
    sort_order  INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- menu — 메뉴
CREATE TABLE menu (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id      BIGINT        NOT NULL REFERENCES place (id),
    name          VARCHAR(100)  NOT NULL,
    name_en       VARCHAR(100),
    price         NUMERIC(10, 0) NOT NULL,
    is_signature  BOOLEAN       NOT NULL DEFAULT FALSE,
    description   VARCHAR(500),
    image_url     VARCHAR(500),
    sort_order    INT           NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- local_score — 현지인 점수 (Place와 1:1)
CREATE TABLE local_score (
    id                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id               BIGINT        NOT NULL REFERENCES place (id),
    total_score            SMALLINT      CHECK (total_score BETWEEN 0 AND 100),
    food_score             NUMERIC(4, 1) CHECK (food_score BETWEEN 0 AND 100),
    price_score            NUMERIC(4, 1) CHECK (price_score BETWEEN 0 AND 100),
    atmosphere_score       NUMERIC(4, 1) CHECK (atmosphere_score BETWEEN 0 AND 100),
    revisit_score          NUMERIC(4, 1) CHECK (revisit_score BETWEEN 0 AND 100),
    local_recommend_score  NUMERIC(4, 1) CHECK (local_recommend_score BETWEEN 0 AND 100),
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uk_local_score_place UNIQUE (place_id)
);

-- place_feature — 편의 정보 (Place와 1:1)
CREATE TABLE place_feature (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id                 BIGINT      NOT NULL REFERENCES place (id),
    english_menu             BOOLEAN     NOT NULL DEFAULT FALSE,
    card_available           BOOLEAN     NOT NULL DEFAULT TRUE,
    solo_friendly            BOOLEAN     NOT NULL DEFAULT FALSE,
    reservation_required     BOOLEAN     NOT NULL DEFAULT FALSE,
    parking_available        BOOLEAN     NOT NULL DEFAULT FALSE,
    avg_wait_time_min        SMALLINT    NOT NULL DEFAULT 0,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_place_feature_place UNIQUE (place_id)
);

-- ai_review_summary — AI 리뷰 요약 (언어별 upsert)
CREATE TABLE ai_review_summary (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id       BIGINT      NOT NULL REFERENCES place (id),
    language       VARCHAR(10) NOT NULL DEFAULT 'en',
    summary        TEXT,
    strengths      TEXT,
    cautions       TEXT,
    generated_at   TIMESTAMPTZ,
    model_version  VARCHAR(50),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_ai_review_summary_place_lang UNIQUE (place_id, language)
);

-- local_tip — 현지인 팁
CREATE TABLE local_tip (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id     BIGINT       NOT NULL REFERENCES place (id),
    content      VARCHAR(500) NOT NULL,
    language     VARCHAR(10)  NOT NULL DEFAULT 'en',
    sort_order   INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- bookmark — 즐겨찾기
CREATE TABLE bookmark (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id   BIGINT      NOT NULL REFERENCES member (id),
    place_id    BIGINT      NOT NULL REFERENCES place (id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_bookmark_member_place UNIQUE (member_id, place_id)
);

-- review — 리뷰 소스 (내부 수집용)
CREATE TABLE review (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    place_id          BIGINT        NOT NULL REFERENCES place (id),
    source            VARCHAR(50)   NOT NULL,
    source_review_id  VARCHAR(255)  NOT NULL,
    content           TEXT,
    rating            NUMERIC(2, 1) CHECK (rating BETWEEN 0 AND 5),
    written_at        TIMESTAMPTZ,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uk_review_source_review_id UNIQUE (source, source_review_id)
);
