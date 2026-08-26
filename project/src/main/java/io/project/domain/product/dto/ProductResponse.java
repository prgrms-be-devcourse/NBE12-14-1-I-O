package io.project.domain.product.dto;

public class ProductResponse {
<<<<<<< HEAD
    public record ProductListResponse(int id, String name, int price, int stock, String imageFileUrl) {
=======
    public record ProductListResponse(int id, String name, int price, int stock, String imageFilename) {
>>>>>>> a81cdbd (feat: 관리자 페이지 상품 목록 출력(백엔드 서버 요청), fix: 백엔드 서버 ProductListResponse DTO(add: stock) 수정)
    }
}
