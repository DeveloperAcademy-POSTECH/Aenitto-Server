package com.firefighter.aenitto.common.exception.mission;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class MissionEmptyException extends CustomException {
    private static final MissionErrorCode CODE = MissionErrorCode.MISSION_EMPTY;

    private MissionEmptyException(ErrorCode code) {
        super(code);
    }

    public MissionEmptyException() {
        this(CODE);
    }
}
