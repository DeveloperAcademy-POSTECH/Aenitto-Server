package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.CustomException;

public class InvalidTokenException extends CustomException {
	private static final AuthErrorCode CODE = AuthErrorCode.INVALID_TOKEN;

	private InvalidTokenException(AuthErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidTokenException() {
		this(CODE);
	}
}
