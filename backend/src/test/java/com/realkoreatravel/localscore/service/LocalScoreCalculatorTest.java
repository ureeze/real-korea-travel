package com.realkoreatravel.localscore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.realkoreatravel.localscore.domain.LocalScore;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Local Score의 동일 가중치 평균과 입력값 검증을 검증한다. */
class LocalScoreCalculatorTest {

    private final LocalScoreCalculator calculator = new LocalScoreCalculator();

    @Test
    @DisplayName("다섯 세부 점수의 평균을 종합 점수로 계산한다")
    void calculatesEqualWeightedTotalScore() {
        LocalScore localScore = localScore("90.0", "80.0", "85.0", "88.0", "85.0");

        Integer result = calculator.calculate(localScore);

        assertThat(result).isEqualTo(86);
    }

    @Test
    @DisplayName("평균의 소수점은 반올림한다")
    void roundsAverageUsingHalfUp() {
        LocalScore localScore = localScore("80.0", "80.0", "80.0", "80.0", "82.5");

        Integer result = calculator.calculate(localScore);

        assertThat(result).isEqualTo(81);
    }

    @Test
    @DisplayName("점수가 0점 미만이면 계산하지 않는다")
    void rejectsScoreBelowZero() {
        LocalScore localScore = localScore("-1.0", "80.0", "80.0", "80.0", "80.0");

        assertThatThrownBy(() -> calculator.calculate(localScore))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("foodScore는 0 이상 100 이하의 값이어야 합니다.");
    }

    @Test
    @DisplayName("세부 점수가 null이면 계산하지 않는다")
    void rejectsNullScore() {
        LocalScore localScore = LocalScore.builder()
                .foodScore(BigDecimal.valueOf(80))
                .priceScore(BigDecimal.valueOf(80))
                .atmosphereScore(BigDecimal.valueOf(80))
                .revisitScore(BigDecimal.valueOf(80))
                .build();

        assertThatThrownBy(() -> calculator.calculate(localScore))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("localRecommendScore는 0 이상 100 이하의 값이어야 합니다.");
    }

    /** 테스트에서 사용할 세부 점수 데이터를 생성한다. */
    private LocalScore localScore(
            String foodScore,
            String priceScore,
            String atmosphereScore,
            String revisitScore,
            String localRecommendScore
    ) {
        return LocalScore.builder()
                .foodScore(new BigDecimal(foodScore))
                .priceScore(new BigDecimal(priceScore))
                .atmosphereScore(new BigDecimal(atmosphereScore))
                .revisitScore(new BigDecimal(revisitScore))
                .localRecommendScore(new BigDecimal(localRecommendScore))
                .build();
    }
}
