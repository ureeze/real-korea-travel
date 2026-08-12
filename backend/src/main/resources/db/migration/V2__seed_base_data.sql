-- RKT-11 베이스 시드 데이터
-- region: 서울(부모) + 성수/홍대/강남/명동 / category: 맛집/카페/디저트/술집
-- code는 URL 파라미터 규칙(소문자)과 일치

INSERT INTO region (name, code, display_order) VALUES
    ('서울', 'seoul', 1);

INSERT INTO region (parent_id, name, code, display_order) VALUES
    ((SELECT id FROM region WHERE code = 'seoul'), '성수', 'seongsu', 1),
    ((SELECT id FROM region WHERE code = 'seoul'), '홍대', 'hongdae', 2),
    ((SELECT id FROM region WHERE code = 'seoul'), '강남', 'gangnam', 3),
    ((SELECT id FROM region WHERE code = 'seoul'), '명동', 'myeongdong', 4);

INSERT INTO category (name, code, display_order) VALUES
    ('맛집',   'restaurant', 1),
    ('카페',   'cafe',       2),
    ('디저트', 'dessert',    3),
    ('술집',   'bar',        4);
