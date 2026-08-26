package io.project.domain.product.dto;

public record ProductAddRequest(String name, int stock, int price, String filename) {
}
