-- RKT-31 기준 지역 데이터
-- 장소 대량 데이터는 seed/places.csv를 PlaceSeedImporter가 적재한다.

INSERT INTO region (name, code, display_order) VALUES
    ('부산', 'busan', 2),
    ('제주', 'jeju', 3);

WITH child_regions(parent_code, name, code, display_order) AS (
    VALUES
        ('busan', '해운대', 'haeundae', 1),
        ('busan', '광안리', 'gwangalli', 2),
        ('busan', '서면', 'seomyeon', 3),
        ('jeju', '제주시', 'jeju-city', 1),
        ('jeju', '서귀포', 'seogwipo', 2),
        ('jeju', '애월', 'aewol', 3)
)
INSERT INTO region (parent_id, name, code, display_order)
SELECT
    parent.id,
    child_regions.name,
    child_regions.code,
    child_regions.display_order
FROM child_regions
JOIN region parent
    ON parent.code = child_regions.parent_code;
