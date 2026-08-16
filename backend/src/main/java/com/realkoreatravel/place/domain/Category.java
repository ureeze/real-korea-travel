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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 카테고리를 식별하는 데이터베이스 기본 키 */
    private Long id;

    @Column(nullable = false, length = 50)
    /** 사용자에게 표시할 카테고리명 */
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    /** API와 DB 조회에 사용하는 카테고리 고유 코드 */
    private String code;

    @Column(name = "display_order", nullable = false)
    /** 카테고리 목록에서 표시할 순서 */
    private int displayOrder;

    @Builder
    public Category(String name, String code, int displayOrder) {
        this.name = name;
        this.code = code;
        this.displayOrder = displayOrder;
    }

}
