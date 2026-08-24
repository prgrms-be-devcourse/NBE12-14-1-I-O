package io.project.domain.product;

import io.project.domain.product.dto.ProductAddRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    /**
     * 상품 추가 POST 요청
     *
     * @param dto 상품 정보 DTO
     * @return HTTP 상태 코드 OK(200) -> 나중에 CREATED로 변경 예정
     */
    @PostMapping
    public ResponseEntity<?> addProduct(ProductAddRequest dto) {
        boolean isSaved = productService.save(dto);

        return ResponseEntity.status(HttpStatus.OK).body(isSaved);
    }
    /*
    상품명 중복 허용되면 아래로 사용하겠습니다.
    @PostMapping
    public ResponseEntity<?> addProduct(ProductAddRequest dto) {
        productService.save(dto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
     */
}
