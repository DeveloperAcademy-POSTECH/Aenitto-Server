package com.firefighter.aenitto.common.exception.message;

import com.firefighter.aenitto.common.exception.CustomException;

public class ImageExtensionNotFoundException extends CustomException {
    private static final MessageErrorCode CODE = MessageErrorCode.IMAGE_EXTENSION_NOT_FOUND;

    private ImageExtensionNotFoundException(MessageErrorCode errorCode) {
        super(errorCode);
    }

    public ImageExtensionNotFoundException() {
        this(CODE);
    }
}
