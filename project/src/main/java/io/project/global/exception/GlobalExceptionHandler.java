package io.project.global.exception;

import io.project.global.dto.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RsData<Void>> businessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400-0", // 비즈니스 공통 에러 코드
                        e.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RsData<Void>> notFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>(
                        "404",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<RsData<Void>> invalidException(InvalidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<RsData<Void>> duplicatedException(DuplicatedException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RsData<>(
                        "409",
                        e.getMessage()
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Void>> noSuchElementException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>(
                        "404-1",
                        "존재하지 않는 데이터입니다."
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> methodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .map(error ->
                        error.getField()
                                + "-"
                                + error.getCode()
                                + "-"
                                + error.getDefaultMessage()
                )
                .sorted()
                .collect(Collectors.joining("\n"));

        return ResponseEntity
                .badRequest()
                .body(new RsData<>(
                        "400-1",
                        message
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RsData<Void>> handleHttpMessageNotReadableException() {
        return ResponseEntity
                .badRequest()
                .body(new RsData<>(
                        "400-2",
                        "잘못된 형식의 요청 데이터입니다."
                ));
    }

}