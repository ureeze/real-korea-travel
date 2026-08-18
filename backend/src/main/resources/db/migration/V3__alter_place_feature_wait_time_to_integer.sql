-- RKT-19 PlaceFeature 평균 대기시간 저장 타입을 INT로 통일
ALTER TABLE place_feature
    ALTER COLUMN avg_wait_time_min TYPE INTEGER;
