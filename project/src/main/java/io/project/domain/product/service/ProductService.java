package io.project.domain.product.service;

import io.project.domain.product.dto.ProductRequest;
import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void updateProduct(Integer id, ProductRequest.ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        product.update(
                request.name(),
                request.price(),
                request.stock(),
                request.fileName()
        );
    }

    @Transactional
    public void orderProduct(Integer id, Integer count) {
        Product product = productRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("주문 수량이 재고보다 많습니다."));

        product.removeStock(count);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        productRepository.delete(product);

//    삭제 시간 기록 시 사용
//    product.delete();

    }
}
