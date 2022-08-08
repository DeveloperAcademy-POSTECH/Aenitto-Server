package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;

public class RoomAlreadyParticipatingException extends CustomException {
    public static final RoomErrorCode CODE = RoomErrorCode.ROOM_ALREADY_PARTICIPATING;

    public RoomAlreadyParticipatingException() {
        this(CODE);
    }

    private RoomAlreadyParticipatingException(RoomErrorCode errorCode) {
        super(errorCode);
    }
}
