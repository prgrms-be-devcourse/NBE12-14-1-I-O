package io.project.domain.product.exception;

import io.project.global.exception.InvalidException;

public class ProductImageInvalidException extends InvalidException {

    public ProductImageInvalidException(Throwable cause) {
        super("잘못된 형식의 이미지입니다.", cause);
    }

    public ProductImageInvalidException(String message) {
        super(message);
    }

    public ProductImageInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
