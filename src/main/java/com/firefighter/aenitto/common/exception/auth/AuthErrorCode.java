package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_IDENTITY_TOKEN(BAD_REQUEST, "유효하지 않은 토큰입니다."),
    APPLE_PUBLIC_KEY_FAILURE(INTERNAL_SERVER_ERROR, "애플 검증키 가져오기 실패")
    ;
    private final HttpStatus status;
    private final String message;
}
