package com.firefighter.aenitto.common.exception.message;

import com.firefighter.aenitto.common.exception.CustomException;

public class RecieverNotFoundException extends CustomException {
	private static final MessageErrorCode CODE = MessageErrorCode.RECIEVER_NOT_FOUND;

	private RecieverNotFoundException(MessageErrorCode errorCode) {
		super(errorCode);
	}

	public RecieverNotFoundException() {
		this(CODE);
	}
}
