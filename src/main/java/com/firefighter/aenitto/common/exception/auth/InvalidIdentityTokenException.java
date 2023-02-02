package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.CustomException;

public class InvalidIdentityTokenException extends CustomException {
	private static final AuthErrorCode CODE = AuthErrorCode.INVALID_IDENTITY_TOKEN;

	private InvalidIdentityTokenException(AuthErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidIdentityTokenException() {
		this(CODE);
	}
}
