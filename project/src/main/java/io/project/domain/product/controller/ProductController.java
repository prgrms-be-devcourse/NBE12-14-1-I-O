package io.project.domain.product.controller;

import io.project.domain.product.entity.Product;
import io.project.domain.product.service.ProductService;
import io.project.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.project.domain.product.dto.ProductResponse.ProductListResponse;

@Tag(name = "상품 API", description = "상품 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 조회
     *
     * @return 전체 상품 목록 DTO 응답
     */
    @Operation(
            summary = "상품 정보 조회",
            description = "현재 판매 중인 모든 상품들을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<RsData<List<ProductListResponse>>> productList() {

        List<Product> products = productService.findAll();

        List<ProductListResponse> response = products.stream().map(product ->
                new ProductListResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock(),
                        "images/" + product.getFileName())
        ).toList();

        return ResponseEntity.ok(new RsData<>(
                "200",
                "상품 목록을 조회했습니다.",
                response));
    }
}