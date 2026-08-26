package io.project.domain.product.controller;

import io.project.domain.product.entity.Product;
import io.project.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.project.domain.product.dto.ProductResponse.ProductListResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 조회
     *
     * @return 전체 상품 목록 DTO 응답
     */
    @GetMapping
    public ResponseEntity<?> productList() {

        List<Product> products = productService.findAll();

        List<ProductListResponse> response = products.stream().map(product ->
                new ProductListResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getFileName())
        ).toList();

        return ResponseEntity.ok(response);
    }
}