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
            try {
                String originalName = image.getOriginalFilename();
                String ext = org.springframework.util.StringUtils.getFilenameExtension(originalName);
                if (ext == null) {
                    throw new InvalidException("올바르지 않은 파일 형식입니다.");
                }
                ext = ext.toLowerCase();

                finalFileName = java.util.UUID.randomUUID().toString() + "." + ext;

                Path targetPath = Path.of(IMAGE_PATH).toAbsolutePath().normalize().resolve(finalFileName);
                if (Files.notExists(targetPath.getParent())) {
                    Files.createDirectories(targetPath.getParent());
                }

                if (product.getFileName() != null) {
                    Path oldPath = Path.of(IMAGE_PATH).toAbsolutePath().normalize().resolve(product.getFileName());
                    Files.deleteIfExists(oldPath);
                }

                Files.write(targetPath, image.getBytes());
            } catch (IOException e) {
                throw new InvalidException("이미지 저장 중 오류가 발생했습니다.", e);
            }
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
    // 주문 생성/수량 증가 시 재고 차감
    public void removeStock(Integer quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new IllegalArgumentException("주문 수량은 재고를 초과할 수 없습니다. 현재 재고: " + this.stock);
        }
        this.stock = restStock;
    }

    // 주문 취소/수량 감소 시 재고 복원
    public void addStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("주문 취소 복구 수량은 0 이하일 수 없습니다.");
        }
        this.stock += quantity;
    }

    public void delete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("이미 판매 중지된 상품입니다.");
        }
        this.deletedAt = LocalDateTime.now();
    }
}
