package io.project.domain.product.dto;

public class ProductResponse {
    public record ProductListResponse(int id, String name, int price, String imageFilename) {
    }
}
