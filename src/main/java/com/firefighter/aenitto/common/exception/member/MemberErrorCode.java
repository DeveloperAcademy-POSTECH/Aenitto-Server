package com.firefighter.aenitto.common.exception.member;

import com.firefighter.aenitto.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(NOT_FOUND, "초대코드가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
