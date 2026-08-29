package io.project.domain.product.entity;

import io.project.global.entity.BaseEntity;
import io.project.global.exception.BusinessException;
import io.project.global.exception.InvalidException;
import io.project.global.exception.OutOfStockException;
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

    @Column(nullable = false, unique = true)
    @NotBlank(message = "상품 이름은 필수이며 공백일 수 없습니다.")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "가격은 필수 항목입니다.")
    private Integer price;

    @Column(nullable = false)
    @NotNull(message = "재고는 필수 항목입니다.")
    private Integer stock;

    @Column
    private LocalDateTime deletedAt;

    private String fileName;

    public void update(String name, Integer price, Integer stock, String fileName) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (stock != null) this.stock = stock;
        if (fileName != null) this.fileName = fileName;
    }

    public void delete() {
        if (this.deletedAt != null) {
            throw new BusinessException("이미 판매 중지된 상품입니다.");
        }
        this.deletedAt = LocalDateTime.now();
    }

    public Product(String name, Integer price, Integer stock, String fileName) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.deletedAt = null;
        this.fileName = fileName;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(
                    "주문 수량은 1개 이상이어야 합니다."
            );
        }

        int restStock = this.stock - quantity;

        if (restStock < 0) {
            throw new OutOfStockException(
                    "주문 수량은 재고를 초과할 수 없습니다. 현재 재고: "
                            + this.stock
            );
        }

        this.stock = restStock;
    }

    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(
                    "복원할 재고 수량은 1개 이상이어야 합니다."
            );
        }

        this.stock += quantity;
    }
}
