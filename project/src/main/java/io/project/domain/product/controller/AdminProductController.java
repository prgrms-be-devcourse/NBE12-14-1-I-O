package io.project.domain.product.controller;

import io.project.domain.product.service.ProductService;
import io.project.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.project.domain.product.dto.ProductRequest.ProductAddRequest;
import static io.project.domain.product.dto.ProductRequest.ProductUpdateRequest;

@Tag(name = "관리자 API", description = "상품 생성, 조회, 취소, 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 정보 수정",
            description = "상품의 이름, 가격, 재고 정보와 첨부 이미지를 새롭게 갱신합니다."
    )
    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "상품 수정 성공",
            content = @Content(examples = @ExampleObject(value = "상품 정보가 성공적으로 수정되었습니다.")))
    public ResponseEntity<String> updateProduct(
            @PathVariable(name = "productId") Integer id,
            @ModelAttribute @Valid ProductUpdateRequest request
    ) {
        productService.updateProduct(id, request, request.image());

        return ResponseEntity.ok("상품 정보가 성공적으로 수정되었습니다.");
    }

    @Operation(
            summary = "상품 비활성화",
            description = "상품을 비활성화하여 상품 조회 목록에서 제외합니다."
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable(name = "productId") Integer id
    ) {
        productService.deleteProduct(id);

        return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
    }

    @Operation(
            summary = "상품 등록",
            description = "상품 정보를 받아 상품 목록에 등록합니다."
    )
    @PostMapping
    public ResponseEntity<RsData<?>> createProduct(
            @ModelAttribute(value = "request") @Valid ProductAddRequest request
    ) {
        productService.save(request);

        return ResponseEntity.ok(new RsData<>(
                "201",
                "상품이 성공적으로 등록되었습니다."));
    }

}
