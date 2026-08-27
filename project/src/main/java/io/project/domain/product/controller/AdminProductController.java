package io.project.domain.product.controller;

import io.project.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.project.domain.product.dto.ProductRequest.ProductAddRequest;
import static io.project.domain.product.dto.ProductRequest.ProductUpdateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @PatchMapping("/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable(name = "productId") Integer id,
            @RequestPart(value = "request") @Valid ProductUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        productService.updateProduct(id, request, image);

        return ResponseEntity.ok("상품 정보가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable(name = "productId") Integer id
    ) {
        productService.deleteProduct(id);

        return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestPart(value = "request") @Valid ProductAddRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        productService.save(request, image);

        return ResponseEntity.ok("상품이 성공적으로 등록되었습니다.");
    }

}
