package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    INVALID_USER_TOKEN(HttpStatus.BAD_REQUEST, "사용자의 토큰정보와 일치하지 않습니다.")
    ;
    private final HttpStatus status;
    private final String message;
}
