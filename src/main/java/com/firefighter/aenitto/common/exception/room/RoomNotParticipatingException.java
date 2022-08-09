package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomNotParticipatingException extends CustomException {
    private static final RoomErrorCode CODE = RoomErrorCode.ROOM_NOT_PARTICIPATING;

    public RoomNotParticipatingException() {
        this(CODE);
    }

    private RoomNotParticipatingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
