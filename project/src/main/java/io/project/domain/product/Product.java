package io.project.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int price;

    private String imageFilename;

    protected Product() {
    }

    public static Product of(String name, int stock, int price, String imageFilename) {
        Product product = new Product();
        product.name = name;
        product.stock = stock;
        product.price = price;
        product.imageFilename = imageFilename;
        return product;
    }
}
