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
                        product.getStock(),
<<<<<<< HEAD
<<<<<<< HEAD
                        "images/" + product.getFileName())
=======
                        product.getFileName())
>>>>>>> a81cdbd (feat: 관리자 페이지 상품 목록 출력(백엔드 서버 요청), fix: 백엔드 서버 ProductListResponse DTO(add: stock) 수정)
=======
                        "images/" + product.getFileName())
>>>>>>> 8c25c46 (feat: 상품 이미지 출력, 백엔드 서버-서버 이미지 자원 접근 허용(WebMvcConfig.java - 이미지 요청 URL: http://localhost:8080/images/파일명))
        ).toList();

        return ResponseEntity.ok(response);
    }
}