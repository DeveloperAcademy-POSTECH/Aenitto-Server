package com.firefighter.aenitto.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException : {}", e.getErrorCode().getMessage());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("BindException : {}", e.getMessage());
        return ErrorResponse.toResponseEntity(e.getBindingResult());
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(MaxUploadSizeExceededException e) {
        log.error("MaxFileSizeException : {}", e.getMessage());
        return ErrorResponse.toResponseEntity(e);
    }
}
