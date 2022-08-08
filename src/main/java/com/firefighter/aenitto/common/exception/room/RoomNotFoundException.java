package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomNotFoundException extends CustomException {
    public static final RoomErrorCode CODE = RoomErrorCode.ROOM_NOT_FOUND;

    public RoomNotFoundException() {
        this(CODE);
    }

    private RoomNotFoundException(ErrorCode code) {
        super(code);
    }
}
