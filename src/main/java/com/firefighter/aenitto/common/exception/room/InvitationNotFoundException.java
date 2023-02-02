package com.firefighter.aenitto.common.exception.room;

import com.firefighter.aenitto.common.exception.CustomException;

public class InvitationNotFoundException extends CustomException {
	private static final RoomErrorCode CODE = RoomErrorCode.INVITATION_NOT_FOUND;

	private InvitationNotFoundException(RoomErrorCode errorCode) {
		super(errorCode);
	}

	public InvitationNotFoundException() {
		this(CODE);
	}
}
