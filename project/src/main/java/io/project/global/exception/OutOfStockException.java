package io.project.global.exception;

public class OutOfStockException extends BusinessException {

    // 기본
    public OutOfStockException() {
        super("재고 수량이 부족합니다.");
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
