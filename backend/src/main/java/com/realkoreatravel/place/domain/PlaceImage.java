package com.realkoreatravel.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 장소 상세 화면에 표시할 이미지 정보를 저장한다. */
@Entity
@Table(name = "place_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "is_main", nullable = false)
    private boolean main;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 장소 이미지와 표시 정보를 생성한다. */
    @Builder
    public PlaceImage(Place place, String imageUrl, boolean main, int sortOrder) {
        this.place = place;
        this.imageUrl = imageUrl;
        this.main = main;
        this.sortOrder = sortOrder;
        if (place != null) {
            place.addImage(this);
        }
        this.createdAt = Instant.now();
    }
}
