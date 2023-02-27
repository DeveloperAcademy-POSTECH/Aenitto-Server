package com.firefighter.aenitto.common.exception.mission;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class MissionNotExistException extends CustomException {
	private static final MissionErrorCode CODE = MissionErrorCode.MISSION_NOT_EXIST;

	private MissionNotExistException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MissionNotExistException() {
		this(CODE);
	}
}