package io.project.domain.product.exception;

import io.project.global.exception.DuplicatedException;

public class ProductNameDuplicatedException extends DuplicatedException {

    public ProductNameDuplicatedException() {
        super("이미 존재하는 상품명입니다.");
    }

    public ProductNameDuplicatedException(String message) {
        super(message);
    }

    public ProductNameDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
