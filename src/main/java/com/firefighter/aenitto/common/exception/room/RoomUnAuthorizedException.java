package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.ErrorCode;

public class RoomUnAuthorizedException extends RoomException {
	public static final RoomErrorCode CODE = RoomErrorCode.ROOM_UNAUTHORIZED;

	public RoomUnAuthorizedException() {
		this(CODE);
	}

	private RoomUnAuthorizedException(ErrorCode errorCode) {
		super(errorCode);
	}
}
