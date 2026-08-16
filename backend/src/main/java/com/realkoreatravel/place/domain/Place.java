package com.realkoreatravel.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 30)
    private String phone;

    @Column(name = "price_level")
    private Integer priceLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "google_place_id", unique = true, length = 255)
    private String googlePlaceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaceStatus status = PlaceStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Builder
    public Place(
            Region region,
            Category category,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String phone,
            Integer priceLevel,
            String description
    ) {
        this.region = region;
        this.category = category;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.priceLevel = priceLevel;
        this.description = description;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** 장소를 영업 종료 상태로 변경하고 수정 시각을 갱신한다. */
    public void close() {
        this.status = PlaceStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    /** 장소를 soft delete 상태로 변경하고 삭제 시각과 수정 시각을 기록한다. */
    public void delete() {
        Instant now = Instant.now();
        this.deletedAt = now;
        this.updatedAt = now;
    }
}
