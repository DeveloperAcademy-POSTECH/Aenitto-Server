package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomInsufficientParticipantsException extends RoomException {
    public static final RoomErrorCode CODE = RoomErrorCode.ROOM_INSUFFICIENT_PARTICIPANTS;

    public RoomInsufficientParticipantsException() {
        this(CODE);
    }
    private RoomInsufficientParticipantsException(ErrorCode code) {
        super(code);
    }
}
