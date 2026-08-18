package com.realkoreatravel.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_feature")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 편의정보를 식별하는 데이터베이스 기본 키. */
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false, unique = true)
    /** 편의정보가 속한 장소. */
    private Place place;

    @Column(name = "english_menu", nullable = false)
    /** 영어 메뉴 제공 여부. */
    private boolean englishMenu;

    @Column(name = "card_available", nullable = false)
    /** 카드 결제 가능 여부. */
    private boolean cardAvailable;

    @Column(name = "solo_friendly", nullable = false)
    /** 혼자 방문하기 좋은 장소인지 여부. */
    private boolean soloFriendly;

    @Column(name = "reservation_required", nullable = false)
    /** 방문 전 예약 필요 여부. */
    private boolean reservationRequired;

    @Column(name = "parking_available", nullable = false)
    /** 주차 가능 여부. */
    private boolean parkingAvailable;

    @Column(name = "avg_wait_time_min", nullable = false)
    /** 평균 대기시간(분). */
    private Integer avgWaitTimeMin;

    @Column(name = "created_at", nullable = false)
    /** 편의정보가 생성된 시각. */
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    /** 편의정보가 마지막으로 수정된 시각. */
    private Instant updatedAt;

    /** 장소의 외국인 편의정보를 생성하고 생성·수정 시각을 초기화한다. */
    @Builder
    public PlaceFeature(
            Place place,
            boolean englishMenu,
            boolean cardAvailable,
            boolean soloFriendly,
            boolean reservationRequired,
            boolean parkingAvailable,
            Integer avgWaitTimeMin
    ) {
        this.place = place;
        this.englishMenu = englishMenu;
        this.cardAvailable = cardAvailable;
        this.soloFriendly = soloFriendly;
        this.reservationRequired = reservationRequired;
        this.parkingAvailable = parkingAvailable;
        this.avgWaitTimeMin = avgWaitTimeMin;
        if (place != null) {
            place.assignFeature(this);
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
