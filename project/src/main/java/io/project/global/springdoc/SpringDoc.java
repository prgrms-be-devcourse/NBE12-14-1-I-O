package io.project.global.springdoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Grids & Circles API",
                version = "v1",
                description = "카페 주문 관리 서비스 API"
        )
)
public class SpringDoc {
        @Bean
        public GroupedOpenApi customerApi() {
                return GroupedOpenApi.builder()
                        .group("01. 고객용 서비스 (Customer)")
                        .pathsToMatch("/products/**", "/orders/**")
                        .build();
        }

        @Bean
        public GroupedOpenApi adminApi() {
                return GroupedOpenApi.builder()
                        .group("02. 관리자 서비스 (Admin)")
                        .pathsToMatch("/admin/**")
                        .build();
        }

}
