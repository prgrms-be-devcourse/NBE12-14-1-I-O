package io.project.domain.product.service;

import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import io.project.global.exception.NotFoundException;
import io.project.global.exception.DuplicatedException;
import io.project.global.exception.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.image.path}")
    private String IMAGE_PATH;

    @Transactional
    public void updateProduct(Integer id, ProductUpdateRequest request, MultipartFile image) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        String finalFileName = product.getFileName();

        if (image != null && !image.isEmpty()) {
            imageSave(image, request.name());

            finalFileName = request.name() + "-Image." + image.getOriginalFilename().split("\\.")[1];
        } else if (!product.getName().equals(request.name()) && finalFileName != null) {

            finalFileName = request.name() + "-Image." + finalFileName.substring(finalFileName.lastIndexOf(".") + 1);
        }

        product.update(
                request.name(),
                request.price(),
                request.stock(),
                finalFileName
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
        return productRepository.findAllByDeletedAtIsNull();
    }

    public void save(ProductAddRequest dto, MultipartFile image) {
        String fileName = (image != null && !image.isEmpty())
                ? imageSave(image, dto.name()) : null;

        Product product = new Product(dto.name(), dto.price(), dto.stock(), fileName);
        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("이미 존재하는 상품명입니다.");
        }
    }

    private String imageSave(MultipartFile image, String productName) {
        String original = image.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        String fileName = productName + "-image." + ext;

        try {
            Path dir = Path.of(IMAGE_PATH).toAbsolutePath().normalize();
            // 디렉토리 존재하지 않으면 생성
            Files.createDirectories(dir);
            Files.write(dir.resolve(fileName), image.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new InvalidException("잘못된 형식의 이미지입니다.", e);
        }
    }
}
