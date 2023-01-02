package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.ErrorCode;

public class AdminCannotExitRoomException extends RoomException {
	private final static ErrorCode CODE = RoomErrorCode.ADMIN_CANNOT_EXIT_ROOM;

	private AdminCannotExitRoomException(ErrorCode code) {
		super(code);
	}

	public AdminCannotExitRoomException() {
		this(CODE);
	}
}
