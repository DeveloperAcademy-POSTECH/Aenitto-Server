package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomAlreadyStartedException extends RoomException {
	public final static RoomErrorCode CODE = RoomErrorCode.ROOM_ALREADY_STARTED;

	public RoomAlreadyStartedException() {
		this(CODE);
	}

	private RoomAlreadyStartedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
