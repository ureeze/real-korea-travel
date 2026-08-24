-- RKT-22 일반 수치 타입 정책에 따라 Local Score 종합 점수를 INTEGER로 변경
ALTER TABLE local_score
    ALTER COLUMN total_score TYPE INTEGER;
