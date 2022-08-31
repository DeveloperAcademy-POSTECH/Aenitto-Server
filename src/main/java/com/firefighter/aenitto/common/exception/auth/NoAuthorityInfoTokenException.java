package com.firefighter.aenitto.common.exception.auth;

import com.firefighter.aenitto.common.exception.CustomException;

public class NoAuthorityInfoTokenException extends CustomException {
    private static final AuthErrorCode CODE = AuthErrorCode.INVALID_TOKEN;

    private NoAuthorityInfoTokenException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public NoAuthorityInfoTokenException() {
        this(CODE);
    }
}
