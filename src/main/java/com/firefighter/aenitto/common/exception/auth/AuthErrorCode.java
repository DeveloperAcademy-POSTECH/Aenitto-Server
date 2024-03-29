package com.firefighter.aenitto.common.exception.auth;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.firefighter.aenitto.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
	INVALID_USER_TOKEN(HttpStatus.BAD_REQUEST, "사용자의 토큰정보와 일치하지 않습니다."),
	INVALID_IDENTITY_TOKEN(BAD_REQUEST, "유효하지 않은 identity 토큰입니다."),
	APPLE_PUBLIC_KEY_FAILURE(INTERNAL_SERVER_ERROR, "애플 검증키 가져오기 실패"),

	NOT_EXPIRED(HttpStatus.BAD_REQUEST, "만료되지 않은 토큰입니다");
	private final HttpStatus status;
	private final String message;
}
