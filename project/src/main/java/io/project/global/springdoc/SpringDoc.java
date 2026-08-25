package io.project.global.springdoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
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

}
