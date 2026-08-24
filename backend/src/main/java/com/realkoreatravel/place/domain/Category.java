package com.realkoreatravel.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    /** 카테고리를 식별하는 데이터베이스 기본 키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 사용자에게 표시할 카테고리명 */
    @Column(nullable = false, length = 50)
    private String name;

    /** API와 DB 조회에 사용하는 카테고리 고유 코드 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 카테고리 목록에서 표시할 순서 */
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder
    public Category(String name, String code, int displayOrder) {
        this.name = name;
        this.code = code;
        this.displayOrder = displayOrder;
    }

}
