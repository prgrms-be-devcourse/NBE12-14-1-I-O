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
                            "Columbia Nariño",
                            4800,
                            100,
                            "Columbia_Nariño-image.png"
                    )
            );

            productRepository.save(
                    new Product(
                            "Brazil Serra Do Caparaó",
                            4500,
                            100,
                            "Brazil_Serra_Do_Caparaó-image.png"
                    )
            );

            productRepository.save(
                    new Product(
                            "Columbia Quindío (White Wine Extended Fermentation)",
                            5000,
                            100,
                            "Columbia_Quindío-image.png"
                    )
            );

            productRepository.save(
                    new Product(
                            "Ethiopia Sidamo",
                            4300,
                            100,
                            "Ethiopia_Sidamo-image.png"
                    )
            );
        };
    }
}