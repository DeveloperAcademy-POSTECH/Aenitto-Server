package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.CustomException;

public class InvalidUserTokenException extends CustomException {
    private static final AuthErrorCode CODE = AuthErrorCode.INVALID_USER_TOKEN;

    private InvalidUserTokenException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidUserTokenException() {
        this(CODE);
    }
}
