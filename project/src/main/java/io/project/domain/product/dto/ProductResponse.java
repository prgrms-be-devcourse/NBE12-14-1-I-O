package io.project.domain.product.dto;

public class ProductResponse {
<<<<<<< HEAD
<<<<<<< HEAD
    public record ProductListResponse(int id, String name, int price, int stock, String imageFileUrl) {
=======
    public record ProductListResponse(int id, String name, int price, int stock, String imageFilename) {
>>>>>>> a81cdbd (feat: 관리자 페이지 상품 목록 출력(백엔드 서버 요청), fix: 백엔드 서버 ProductListResponse DTO(add: stock) 수정)
=======
    public record ProductListResponse(int id, String name, int price, int stock, String imageFileUrl) {
>>>>>>> 8c25c46 (feat: 상품 이미지 출력, 백엔드 서버-서버 이미지 자원 접근 허용(WebMvcConfig.java - 이미지 요청 URL: http://localhost:8080/images/파일명))
    }
}
