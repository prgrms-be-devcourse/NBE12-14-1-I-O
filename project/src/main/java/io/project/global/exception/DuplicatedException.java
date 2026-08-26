package io.project.global.exception;

public class DuplicatedException extends BusinessException{
    public DuplicatedException(String message) {
        super(message);
    }

    public DuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
