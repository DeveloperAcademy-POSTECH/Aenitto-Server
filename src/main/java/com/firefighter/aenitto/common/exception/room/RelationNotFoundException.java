package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class RelationNotFoundException extends CustomException  {
    private final static RoomErrorCode CODE = RoomErrorCode.RELATION_NOT_FOUND;

    private RelationNotFoundException(ErrorCode code) {
        super(code);
    }

    public RelationNotFoundException() {
        this(CODE);
    }
}
