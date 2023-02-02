package com.firefighter.aenitto.common.exception.mission;

import com.firefighter.aenitto.common.exception.CustomException;

public class MissionAlreadySetException extends CustomException {
	private static final MissionErrorCode CODE = MissionErrorCode.MISSION_ALREADY_SET;

	private MissionAlreadySetException(MissionErrorCode code) {
		super(code);
	}

	public MissionAlreadySetException() {
		this(CODE);
	}
}
