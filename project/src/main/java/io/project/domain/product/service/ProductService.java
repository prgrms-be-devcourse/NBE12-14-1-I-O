package io.project.domain.product.service;

import io.project.domain.product.dto.ProductRequest;
import io.project.domain.product.entity.Product;
import io.project.domain.product.exception.ProductNameDuplicatedException;
import io.project.domain.product.repository.ProductRepository;
import io.project.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void updateProduct(Integer id, ProductRequest.ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        product.update(
                request.name(),
                request.price(),
                request.stock(),
                request.fileName()
        );
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        product.delete();

    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void save(ProductRequest.ProductAddRequest dto, MultipartFile image) {
        Product product = new Product(dto.name(), dto.price(), dto.stock(), dto.filename());
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new ProductNameDuplicatedException();
        }
    }
}
