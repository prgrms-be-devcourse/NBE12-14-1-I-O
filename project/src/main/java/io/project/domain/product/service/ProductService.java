package io.project.domain.product.service;

import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import io.project.global.exception.NotFoundException;
import io.project.global.exception.DuplicatedException;
import io.project.global.exception.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static io.project.domain.product.dto.ProductRequest.*;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final String IMAGE_PATH = "src/main/java/io/project/domain/product/images/";

    @Transactional
    public void updateProduct(Integer id, ProductUpdateRequest request) {

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
    public Product decreaseStockForOrder(Integer id, Integer count) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("존재하지 않는 상품입니다.")
                );
        product.decreaseStock(count);
        return product;
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

    public void save(ProductAddRequest dto, MultipartFile image) {
        String fileName = null;
        if (image != null) {
            imageSave(image, dto.name());
            fileName = dto.name() + "-Image." + image.getOriginalFilename().split("\\.")[1];
        }

        Product product = new Product(dto.name(), dto.price(), dto.stock(), fileName);
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("이미 존재하는 상품명입니다.");
        }
    }

    private void imageSave(MultipartFile image, String productName) {
        try {
            // 디렉토리 존재하지 않으면 생성
            if (Files.notExists(Path.of(IMAGE_PATH))) {
                Files.createDirectory(Path.of(IMAGE_PATH));
            }

            Files.write(Path.of(
                    IMAGE_PATH + productName + "-Image." + image.getOriginalFilename().split("\\.")[1]),
                    image.getBytes());
        } catch (IOException e) {
            throw new InvalidException("잘못된 형식의 이미지입니다.", e);
        }
    }
}
