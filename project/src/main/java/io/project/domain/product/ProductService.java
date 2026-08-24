package io.project.domain.product;

import io.project.domain.product.dto.ProductAddRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public boolean save(ProductAddRequest dto) {
        Product product = Product.of(dto.name(), dto.stock(), dto.price(), dto.filename());

        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            // 나중에 GlobalExceptionHandler 설정하고 예외 메시지 설정해서 넘기기
            System.out.println("중복되는 이름입니다.");
            return false;
        }
        return true;
    }
    /*
    상품명 중복 허용하면 아래로 사용하겠습니다.
    public void save(ProductAddRequest dto) {
        Product product = Product.of(dto.name(), dto.stock(), dto.price(), dto.filename());
        productRepository.save(product);
    }
    */
}
