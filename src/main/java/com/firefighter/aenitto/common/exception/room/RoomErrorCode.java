package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum RoomErrorCode implements ErrorCode {
    INVITATION_NOT_FOUND(NOT_FOUND, "초대코드가 존재하지 않습니다."),

    ROOM_ALREADY_PARTICIPATING(CONFLICT, "이미 참여 중인 방입니다."),
    ROOM_NOT_FOUND(NOT_FOUND, "방이 존재하지 않습니다."),
    ROOM_CAPACITY_EXCEEDED(BAD_REQUEST, "방의 수용인원을 초과하였습니다.")
    ;
    private final HttpStatus status;
    private final String message;
}
