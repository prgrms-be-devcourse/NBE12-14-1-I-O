package io.project.domain.product.service;

import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import io.project.global.exception.NotFoundException;
import io.project.global.exception.DuplicatedException;
import io.project.global.exception.InvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static io.project.domain.product.dto.ProductRequest.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final String IMAGE_PATH;

    public ProductService(ProductRepository productRepository, @Value("${app.image.path}")String IMAGE_PATH) {
        this.productRepository = productRepository;
        this.IMAGE_PATH = IMAGE_PATH;
    }

    @Transactional
    public void updateProduct(
            Integer id,
            ProductUpdateRequest request,
            MultipartFile image
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("존재하지 않는 상품입니다.")
                );

        String finalFileName = product.getFileName();

        if (image != null && !image.isEmpty()) {
            String oldFileName = product.getFileName();

            finalFileName = imageSave(image);

            deleteImage(oldFileName);
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

    public Product save(ProductAddRequest dto) {
        MultipartFile image = dto.image();

        String fileName = (image != null && !image.isEmpty())
                ? imageSave(image)
                : null;

        Product product = new Product(
                dto.name(),
                dto.price(),
                dto.stock(),
                fileName
        );

        try {
            productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedException("이미 존재하는 상품명입니다.");
        }

        return product;
    }

    private String imageSave(MultipartFile image) {
        String originalName = image.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(originalName);

        if (ext == null || ext.isBlank()) {
            throw new InvalidException("올바르지 않은 파일 형식입니다.");
        }

        ext = ext.toLowerCase();

        String fileName = UUID.randomUUID() + "." + ext;

        try {
            Path dir = Path.of(IMAGE_PATH)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(dir);

            Files.write(
                    dir.resolve(fileName),
                    image.getBytes()
            );

            return fileName;
        } catch (IOException e) {
            throw new InvalidException(
                    "이미지 저장 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    private void deleteImage(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Path imagePath = Path.of(IMAGE_PATH)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(fileName);

            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new InvalidException(
                    "기존 이미지 삭제 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    @Transactional
    public Product decreaseStockForOrder(
            int productId,
            int quantity
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new NotFoundException("상품을 찾을 수 없습니다.")
                );

        product.decreaseStock(quantity);

        return product;
    }

    @Transactional
    public void increaseStockForCancel(
            int productId,
            int quantity
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new NotFoundException("상품을 찾을 수 없습니다.")
                );

        product.increaseStock(quantity);
    }
}
