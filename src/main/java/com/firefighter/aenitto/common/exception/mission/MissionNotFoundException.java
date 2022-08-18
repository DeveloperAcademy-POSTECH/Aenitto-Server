package com.firefighter.aenitto.common.exception.mission;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class MissionNotFoundException extends CustomException  {
    private static final MissionErrorCode CODE = MissionErrorCode.MISSION_NOT_FOUND;

    private MissionNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MissionNotFoundException() {
        this(CODE);
    }
}
