package com.realkoreatravel.localscore.service;

import com.realkoreatravel.localscore.domain.LocalScore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/** Local Score의 세부 점수를 100점 만점 종합 점수로 변환한다. */
@Component
public class LocalScoreCalculator {

    private static final BigDecimal MIN_SCORE = BigDecimal.ZERO;
    private static final BigDecimal MAX_SCORE = BigDecimal.valueOf(100);
    private static final BigDecimal SCORE_COUNT = BigDecimal.valueOf(5);

    /** 다섯 세부 점수의 동일 가중치 평균을 계산해 정수 점수로 반환한다. */
    public Integer calculate(LocalScore localScore) {
        validateScore("foodScore", localScore.getFoodScore());
        validateScore("priceScore", localScore.getPriceScore());
        validateScore("atmosphereScore", localScore.getAtmosphereScore());
        validateScore("revisitScore", localScore.getRevisitScore());
        validateScore("localRecommendScore", localScore.getLocalRecommendScore());

        return localScore.getFoodScore()
                .add(localScore.getPriceScore())
                .add(localScore.getAtmosphereScore())
                .add(localScore.getRevisitScore())
                .add(localScore.getLocalRecommendScore())
                .divide(SCORE_COUNT, 0, RoundingMode.HALF_UP)
                .intValueExact();
    }

    /** null이 아니고 0점부터 100점 사이인지 검증한다. */
    private void validateScore(String fieldName, BigDecimal score) {
        if (score == null
                || score.compareTo(MIN_SCORE) < 0
                || score.compareTo(MAX_SCORE) > 0) {
            throw new IllegalArgumentException(fieldName + "는 0 이상 100 이하의 값이어야 합니다.");
        }
    }
}
