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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    /** 장소를 식별하는 데이터베이스 기본 키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 장소가 속한 지역 분류 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    /** 장소의 업종·분류 정보 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** 사용자에게 표시할 장소명 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 장소가 위치한 도로명 또는 지번 주소 */
    @Column(nullable = false, length = 255)
    private String address;

    /** 장소의 위도 좌표 */
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    /** 장소의 경도 좌표 */
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    /** 장소에 연락할 수 있는 전화번호 */
    @Column(length = 30)
    private String phone;

    /** 가격대를 1~4 단계로 표현한 값 */
    @Column(name = "price_level")
    private Short priceLevel;

    /** 장소에 대한 상세 설명 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Google Places API에서 사용하는 외부 장소 식별자 */
    @Column(name = "google_place_id", unique = true, length = 255)
    private String googlePlaceId;

    /** 장소의 운영 상태와 노출 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaceStatus status = PlaceStatus.ACTIVE;

    /** 장소 데이터가 생성된 시각 */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 장소 데이터가 마지막으로 수정된 시각 */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** soft delete 처리된 시각; null이면 삭제되지 않은 상태 */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** 장소에 등록된 메뉴 목록이며 표시 순서 기준으로 정렬된다. */
    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, id ASC")
    private List<Menu> menus = new ArrayList<>();

    @Builder
    public Place(
            Region region,
            Category category,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String phone,
            Short priceLevel,
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

    /** Menu 생성 시 장소의 메뉴 컬렉션에 새 메뉴를 추가한다. */
    void addMenu(Menu menu) {
        this.menus.add(menu);
    }
}
