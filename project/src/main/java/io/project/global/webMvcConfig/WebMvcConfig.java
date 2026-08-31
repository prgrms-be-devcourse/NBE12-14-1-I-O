package io.project.global.webMvcConfig;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String imagePath;

    public WebMvcConfig(
            @Value("${app.image.path}") String imagePath
    ) {
        this.imagePath = imagePath;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:3000",
                        "https://www.devcproject.cloud/"
                )

                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false); // 로그인 기능 구현 시 true로 변경
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 설정된 이미지 저장 경로를 정적 리소스 경로로 등록
        Path uploadPath = Path.of(imagePath)
                .toAbsolutePath()
                .normalize();

        String resourceLocation = uploadPath.toUri().toString();

        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}