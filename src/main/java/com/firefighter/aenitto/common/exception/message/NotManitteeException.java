package com.firefighter.aenitto.common.exception.message;

import com.firefighter.aenitto.common.exception.CustomException;

public class NotManitteeException extends CustomException {
	private static final MessageErrorCode CODE = MessageErrorCode.NOT_MY_MANITTEE;

	private NotManitteeException(MessageErrorCode errorCode) {
		super(errorCode);
	}

	public NotManitteeException() {
		this(CODE);
	}
}
