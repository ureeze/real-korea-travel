package com.realkoreatravel.localscore.domain;

import com.realkoreatravel.place.domain.Place;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 장소에 대한 현지인 관점의 세부 점수를 저장하는 엔티티다. */
@Entity
@Table(name = "local_score")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalScore {

    /** Local Score를 식별하는 데이터베이스 기본 키. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 점수가 매겨진 장소. 한 장소에는 하나의 Local Score만 연결된다. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false, unique = true)
    private Place place;

    /** 음식·가격·분위기·재방문 점수를 종합한 정수형 점수. */
    @Column(name = "total_score")
    private Integer totalScore;

    /** 음식 품질에 대한 세부 점수. */
    @Column(name = "food_score", precision = 4, scale = 1)
    private BigDecimal foodScore;

    /** 가격 적정성에 대한 세부 점수. */
    @Column(name = "price_score", precision = 4, scale = 1)
    private BigDecimal priceScore;

    /** 장소 분위기에 대한 세부 점수. */
    @Column(name = "atmosphere_score", precision = 4, scale = 1)
    private BigDecimal atmosphereScore;

    /** 재방문 의향에 대한 세부 점수. */
    @Column(name = "revisit_score", precision = 4, scale = 1)
    private BigDecimal revisitScore;

    /** 검색의 현지인 추천 필터에 사용하는 파생 점수. */
    @Column(name = "local_recommend_score", precision = 4, scale = 1)
    private BigDecimal localRecommendScore;

    /** Local Score가 생성된 시각. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Local Score가 마지막으로 수정된 시각. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Local Score에서 장소로 향하는 단방향 1:1 관계를 연결하고 생성·수정 시각을 초기화한다. */
    @Builder
    public LocalScore(
            Place place,
            Integer totalScore,
            BigDecimal foodScore,
            BigDecimal priceScore,
            BigDecimal atmosphereScore,
            BigDecimal revisitScore,
            BigDecimal localRecommendScore
    ) {
        this.place = place;
        this.totalScore = totalScore;
        this.foodScore = foodScore;
        this.priceScore = priceScore;
        this.atmosphereScore = atmosphereScore;
        this.revisitScore = revisitScore;
        this.localRecommendScore = localRecommendScore;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** 계산된 종합 점수를 저장하고 변경 시각을 갱신한다. */
    public void updateTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
        this.updatedAt = Instant.now();
    }
}
