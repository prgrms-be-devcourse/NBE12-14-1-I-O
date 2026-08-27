package io.project.global.initData;

import io.project.domain.product.entity.Product;
import io.project.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProductInitData {

    private final ProductRepository productRepository;

    @Bean
    ApplicationRunner productInitDataRunner() {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            productRepository.save(
                    new Product(
                            "에티오피아 예가체프",
                            4800,
                            100,
                            "ethiopia.jpg"
                    )
            );

            productRepository.save(
                    new Product(
                            "콜롬비아 수프리모",
                            4500,
                            100,
                            "colombia.jpg"
                    )
            );

            productRepository.save(
                    new Product(
                            "과테말라 안티구아",
                            5000,
                            100,
                            "guatemala.jpg"
                    )
            );

            productRepository.save(
                    new Product(
                            "브라질 산토스",
                            4300,
                            100,
                            "brazil.jpg"
                    )
            );
        };
    }
}