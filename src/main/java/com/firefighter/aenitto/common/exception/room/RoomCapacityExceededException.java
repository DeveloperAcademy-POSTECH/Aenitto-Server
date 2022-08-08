package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomCapacityExceededException extends CustomException {
    public static final RoomErrorCode CODE = RoomErrorCode.ROOM_CAPACITY_EXCEEDED;

    public RoomCapacityExceededException() {
        this(CODE);
    }

    private RoomCapacityExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
