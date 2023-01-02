package com.firefighter.aenitto.common.exception.test;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.firefighter.aenitto.common.exception.ErrorCode;

public enum TestErrorCode implements ErrorCode {
	TEST_ERROR_CODE(BAD_REQUEST, "테스트 에러 메시지 입니다");
	private final HttpStatus status;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	TestErrorCode(HttpStatus httpStatus, String message) {
		this.status = httpStatus;
		this.message = message;
	}
}
