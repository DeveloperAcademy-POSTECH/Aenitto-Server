package com.firefighter.aenitto.common.exception.message;

import com.firefighter.aenitto.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements ErrorCode {
    IMAGE_EXTENSION_NOT_FOUND(UNSUPPORTED_MEDIA_TYPE, "이미지 확장자 명을 찾을 수 없습니다."),
    RECIEVER_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자에게 메시지를 보낼 수 없습니다."),

    FILE_UPLOAD_ERROR(INTERNAL_SERVER_ERROR, "파일 업로드중 예외가 발생하였습니다"),
    ;
    private final HttpStatus status;
    private final String message;
}
