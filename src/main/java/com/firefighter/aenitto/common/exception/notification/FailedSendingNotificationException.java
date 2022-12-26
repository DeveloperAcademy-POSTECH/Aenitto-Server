package com.firefighter.aenitto.common.exception.notification;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.common.exception.ErrorCode;

public class FailedSendingNotificationException extends CustomException {
	private static final NotificationErrorCode CODE = NotificationErrorCode.FAILED_SENDING_NOTIFICATION;

	private FailedSendingNotificationException(ErrorCode code) {
		super(code);
	}

	public FailedSendingNotificationException() {
		this(CODE);
	}
}