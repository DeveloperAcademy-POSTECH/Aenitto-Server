package com.firefighter.aenitto.common.exception.message;

import com.firefighter.aenitto.common.exception.CustomException;

public class FileUploadException extends CustomException {
    private static final MessageErrorCode CODE = MessageErrorCode.FILE_UPLOAD_ERROR;

    private FileUploadException(MessageErrorCode errorCode) {
        super(errorCode);
    }

    public FileUploadException() {
        this(CODE);
    }
}
