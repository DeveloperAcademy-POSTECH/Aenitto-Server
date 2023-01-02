package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.CustomException;

public class TokenNotExpired extends CustomException {
	private static final AuthErrorCode CODE = AuthErrorCode.NOT_EXPIRED;

	private TokenNotExpired(AuthErrorCode errorCode) {
		super(errorCode);
	}

	public TokenNotExpired() {
		this(CODE);
	}
}
