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

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAllByDeletedAtIsNull();
    }

    @Transactional
    public void updateProduct(Integer id, ProductUpdateRequest request, MultipartFile image) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));

        String fileName = null;

        if (image == null && product.getFileName() != null) {
            String targetFileName = request.name() + "-image." + product.getFileName().split("\\.")[1];
            imagePathMove(product.getFileName(), targetFileName);
            fileName = targetFileName;
        }
        else if (image != null) {
            if (product.getFileName() != null) {
                imageRemove(product.getFileName());
            }
            imageSave(image, request.name());
            fileName = request.name() + "-image." + image.getOriginalFilename().split("\\.")[1];
        }

        product.update(
                request.name(),
                request.price(),
                request.stock(),
                fileName
        );
    }

    @Transactional
    public void save(ProductAddRequest dto, MultipartFile image) {
        String fileName = null;
        if (image != null) {
            imageSave(image, dto.name());
            fileName = dto.name() + "-image." + image.getOriginalFilename().split("\\.")[1];
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
                    IMAGE_PATH + productName + "-image." + image.getOriginalFilename().split("\\.")[1]),
                    image.getBytes());
        } catch (IOException e) {
            throw new InvalidException("잘못된 형식의 이미지입니다.", e);
        }
    }

    private void imageRemove(String fileName) {
        try {
            // 디렉토리 존재하지 않으면 생성
            if (Files.notExists(Path.of(IMAGE_PATH))) {
                Files.createDirectory(Path.of(IMAGE_PATH));
            }

            // 이미지 삭제
            Files.delete(Path.of(IMAGE_PATH + fileName));

        } catch (IOException e) {
            throw new InvalidException("서버에 저장된 이미지 삭제를 실패했습니다.", e);
        }
    }

    private void imagePathMove(String sourceFileName, String targetFileName) {
        try {
            // 디렉토리 존재하지 않으면 생성
            if (Files.notExists(Path.of(IMAGE_PATH))) {
                Files.createDirectory(Path.of(IMAGE_PATH));
            }

            // 이미지 이동
            Files.move(
                    Path.of(IMAGE_PATH + sourceFileName),
                    Path.of(IMAGE_PATH + targetFileName)
            );

        } catch (IOException e) {
            throw new InvalidException("서버에 저장된 이미지 삭제를 실패했습니다.", e);
        }
    }
}
