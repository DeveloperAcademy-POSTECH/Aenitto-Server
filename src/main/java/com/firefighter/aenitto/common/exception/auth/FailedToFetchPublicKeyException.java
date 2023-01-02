package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.CustomException;

public class FailedToFetchPublicKeyException extends CustomException {
	private static final AuthErrorCode CODE = AuthErrorCode.APPLE_PUBLIC_KEY_FAILURE;

	private FailedToFetchPublicKeyException(AuthErrorCode errorCode) {
		super(errorCode);
	}

	public FailedToFetchPublicKeyException() {
		this(CODE);
	}
}
