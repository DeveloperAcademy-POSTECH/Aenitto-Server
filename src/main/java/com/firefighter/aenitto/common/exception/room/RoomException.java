package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomException extends CustomException {
    public RoomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
