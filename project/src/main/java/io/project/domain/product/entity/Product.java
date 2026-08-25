package io.project.domain.product.entity;

import io.project.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Column(nullable = false)
    @NotBlank(message = "상품 이름은 필수이며 공백일 수 없습니다.")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "가격은 필수 항목입니다.")
    private Integer price;

    @Column(nullable = false)
    @NotNull(message = "재고는 필수 항목입니다.")
    private Integer stock;

    /* 삭제 시간 기록 시 사용 (or BaseEntity)
    @Column
    private LocalDateTime deletedAt;
     */

    private String fileName;

    public void update(String name, Integer price, Integer stock, String fileName) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (stock != null) this.stock = stock;
        if (fileName != null) this.fileName = fileName;
    }

    public void removeStock(Integer quantity) {
        int restStock = this.stock - quantity;

        if (restStock < 0) {
            throw new IllegalArgumentException("주문 수량은 재고를 초과할 수 없습니다. 현재 재고: " + this.stock);
        }

        this.stock = restStock;
    }

    /* 삭제 시간 기록 시 사용
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
     */
}
